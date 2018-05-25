/**
 * Messenger module.<br>
 * 
 * Messenger is for subscribing message channels.
 */
;
(function(window, $, Logger, Lang, Common, atmosphere, _, undefined) {
	"use strict"

	if (window.Messenger) {
		return;
	}

	var logger = new Logger() || $.logger || Common.logger || new Logger(),
		heredoc = Lang.heredoc;

	var SOCKET_OPTIONS = {
		contentType : "application/json",
		logLevel : logger.level,
		transport : "websocket",
		trackMessageLength : true,
		reconnectInterval : 5000
	};

	function normalizeChannel(channel) {
		if (!channel || !_.isString(channel) || _.isEmpty(_.trim(channel))) {
			throw "channel is illegal: " + channel;
		}
		return ("/" + _.trim(channel)).replace(/^\/+/g, '/');
	}
	
	function Subscription(request) {
		var self = this;
		
		this.request = request;
		this.messageCallbacks = {};
		
		function dispatchMessage(type, message) {
			logger.debug("dispatchMessage: ", arguments);
			
			var callbacksForAll = self.messageCallbacks["*"],
				callbacksForOne = self.messageCallbacks[type];
			
			_.forEach(callbacksForOne || [], function(callback) {
				callback.call(self, message);
			});
			
			_.forEach(callbacksForAll || [], function(callback) {
				callback.call(self, type, message);
			});
		}
		
		request.onMessage = function(response) {
			logger.debug('got response: ', response);
			var message, type;
			
			try {
				message = $.parseJSON(response.responseBody);
			} catch (e) {
				logger.error("response does not seem to be a valid json: ", response.responseBody);
				logger.error(e);
				return;
			}
			
			type = message.__type__;
			delete message.__type__;
			
			dispatchMessage(type, message);
		};
		
		request.onOpen = function(response) {
			// Carry the UUID. This is required if you want to call subscribe(request) again.
			request.uuid = response.request.uuid;
		};
		
		this.socket = atmosphere.subscribe(request);
	}
	
	/**
	 * Unsubscribe the subscription.
	 */
	Subscription.prototype.unsubscribe = function() {
		this.socket.unsubscribe();
		return this;
	};
	
	/**
	 * Register callback for one type of messages or all types of messages.<br>
	 * eg: <br>
	 * subscription.onMessage(function(type, message) {}); // listen for all types of messages<br>
	 * subscription.onMessage("DeploymentProgress, "function(message) {}); // listen for DeploymentProgress messages<br>
	 */
	Subscription.prototype.onMessage = function(type, callback) {
		if (arguments.length === 0) {
			throw "onMessage: illegal arguments, no callback is given";
		} else if (arguments.length === 1) {
			callback = type;
			type = "*";
		}
		if (!_.isString(type) || !_.isFunction(callback)) {
			throw "onMessage: illegal arguments, only onMessage(String type, Function callback) or onMessage(Function callback) is accepted";
		}
		var callbacks = this.messageCallbacks[type];
		if (callbacks == null) {
			callbacks = this.messageCallbacks[type] = [];
		}
		callbacks.push(callback);
		return this;
	};
	
	/**
	 * Subscribe for a message channel.
	 * 
	 * @param channel channel to subscribe
	 * 
	 * @return Subscription
	 */
	function subscribe(channel) {
		var subSocket,
			request = $.extend({}, SOCKET_OPTIONS);
		
		channel = normalizeChannel(Lang.interpolate.apply(null, arguments));
		
		request.url = "/messages" + channel;
		logger.debug("subscribing to channel[" + channel + "]");
		return new Subscription(request);
	}
	
	var $body = $(window),
		callbackWrappers = {},
		seq = new Date().getTime(),
		allChannelCallbacks = {};
	
	/* allChannelCallbacks has following structure:
	{
		"channel1": {
			"type1": [callback1, callback2],
			"type2": [callback3]
		},
		"channel2": {
			"type3": [callback1, callback2]
		}
		// ...
	}
	*/
	
	subscribe("/*").onMessage(function(type, message) {
		logger.debug("onMessage", arguments);
		var channel = message.__channel__;
		delete message.__channel__;
		//$body.trigger(channel, [type, message]);
		var channelCallbacks = allChannelCallbacks[channel],
			typeCallbacks;
		
		if (channelCallbacks != null) {
			typeCallbacks = channelCallbacks[type];
			logger.debug("in onMessage, channel: ", channel, ", type: ", type, ", typeCallbacks: ", typeCallbacks);
			_.forEach(typeCallbacks || [], function(callback) {
				callback(message);
			});
		} else {
			logger.warn("no callbacks found for ", channel);
		}
	});
	
	function on(channel, type, callback) {
		logger.debug("on", arguments);
		callback.__seq__ = callback.__seq__ || (seq++);
		
		var channelCallbacks = allChannelCallbacks[channel],
			typeCallbacks;
		if (channelCallbacks == null) {
			channelCallbacks = allChannelCallbacks[channel] = {};
		}
		typeCallbacks = channelCallbacks[type];
		if (typeCallbacks == null) {
			typeCallbacks = channelCallbacks[type] = [];
		}
		typeCallbacks.push(callback);
		
		return this;
	}
	
	function off(channel, type, callback) {
		logger.debug("off", arguments);
		var channelCallbacks = allChannelCallbacks[channel],
			typeCallbacks;
		
		if (channelCallbacks != null) {
			typeCallbacks = channelCallbacks[type];
			if (typeCallbacks != null) {
				_.remove(typeCallbacks, function(c) {
					return callback.__seq__ === c.__seq__;
				});
			}
		}
		
		return this;
	}
	
	window.Messenger = {
		on: on,
		off: off,
		allChannelCallbacks: allChannelCallbacks
	};
})(this, jQuery, Logger, Lang, window.Common || {}, $.atmosphere || window.atmosphere, _);