/**
 * Topic module.<br>
 * 
 */
;
(function(window, $, I18N, Logger, Lang, Common, Messenger, atmosphere, _, undefined) {
	"use strict"

	if (window.Topic) {
		return;
	}

	var logger = new Logger() || $.logger || Common.logger || new Logger(),
		heredoc = Lang.heredoc;
	
	var TOPIC_DEFINITIONS = {};
	
	//定义I18N英转中的函数
	I18N.m = function(messageKey) {
		return I18N.i(messageKey).indexOf("unknown key:") >= 0 ? I18N.i("ASYNC_UNKNOW") : I18N.i(messageKey);
	};
	
	//定义异步消息弹框的行元素基本样式
	var ROW_TEMPLATE = _.template(heredoc(function() {/*
		<li data-id="{%- topic.id %}"
			data-type="{%- topic.__type__ %}"
			data-channel="{%- topic.channel %}"
			data-url="{%- topic.url %}"
			class="topic {%= topic.closed ? 'topic-closed' : '' %}">
			<a href="#">
				{%  if (topic.content.status == "doing") {  %}
					<i class="icon icon12 ing"></i><span class="unFw txtOf">{%= definition.render(topic) %}</span>
				{% } %}
				{% if (topic.content.status == "success") { %}
					<i class="icon icon12 success"></i><span class="txtOf">{%= definition.render(topic) %}</span><i class="icon icon12 delb"></i>
				{% } %}
				{% if (topic.content.status == "failure") { %}
					<i class="icon icon12 failure"></i><span class="txtOf">{%= definition.render(topic) %}</span><i class="icon icon12 delb"></i>
				{% } %}
				<span class="fr mr30">{%= formateDate(topic.createdAt) %}</span>
			</a>
		</li>
	*/}));
//	<em>{%= formateDate(topic.createdAt) %}</em>
	//加载异步消息内容
	var LIST_TEMPLATE = _.template(heredoc(function() {/*
		<p>任务列表<a class="fr c-197" id="clean" href="#">清除全部</a></p>
		<ul class="run_list topics">
			{% _.forEach(topics, function(topic) { %}
				{%= renderRow(topic) %}
			{% }); %}
			<li class="no-topics" style="{%= topics && topics.length > 0 ? 'display: none' : '' %}">{%- I18N.i('ui.topic.list.empty') %}</li>
		</ul>
	*/}));
	
	function registerTopicDefinition(type, topicDefinition) {
		if (!(_.isFunction(topicDefinition.render) && _.isFunction(topicDefinition.bindEvents))) {
			throw "render should have render and bindEvents methods";
		}
		TOPIC_DEFINITIONS[type] = topicDefinition;
	}
	
	//定义时间的显示
	function formateDate(date) {
		var curDate=new Date(date);
		var curHour=curDate.getHours();
		var curMinute=curDate.getMinutes();
		var curSecond=curDate.getSeconds();
		curHour=curHour<10?("0"+curHour):curHour;
		curMinute=curMinute<10?("0"+curMinute):curMinute;
		curSecond=curSecond<10?("0"+curSecond):curSecond;
		return	curHour+":"+curMinute+":"+curSecond;
	}
	function refreshTopicsDisplay() {
		
		var lis=$("#topics-container li.topic");
		var count = lis.length;
		if(lis.has(".ing").length>0){
			$("#topics-count").addClass("runing").removeClass("notice");
		}else{
			$("#topics-count").removeClass("runing").addClass("notice");
		}
		$("#topics-container .no-topics")[count == 0 ? 'show' : 'hide']();
	 
	}
	
	function renderRow(topic) {
		var type = topic.__type__,
			channel = topic.channel,
			definition = TOPIC_DEFINITIONS[type];
		
		return ROW_TEMPLATE({
			"topic": topic,
			"definition": definition,
			"formateDate": formateDate
		});
	}
	
	function bindRowEvents($row) {
		var type = $row.data("type"),
			definition = TOPIC_DEFINITIONS[type];
		
		definition.bindEvents($row);
	}
	
	function bindRowMessages($row) {
		var type = $row.data("type"),
			definition = TOPIC_DEFINITIONS[type],
			channel = $row.data("channel");
	
		_.forEach((definition.messages || {}), function(callback, type) {
			logger.debug("binding message for row: ", channel, type, definition);
			Messenger.on(channel, type, callback);
		});
	}
	
	function unbindRowMessages($row) {
		var type = $row.data("type"),
		definition = TOPIC_DEFINITIONS[type],
		channel = $row.data("channel");

		_.forEach(definition.messages, function(callback, type) {
			Messenger.off(channel, type, callback);
		});
	}
	
	function refreshTopics(callback) {
		$.get($.pageRoot + "topic/index.do").done(function(data) {
			var topics = data.topics,
				html = LIST_TEMPLATE({
					"I18N": I18N,
					"topics": topics,
					"renderRow": renderRow
				});
			
			$("#topics-container").html(html);
			$("#topics-container li.topic").each(function() {
				var $row = $(this),
					closed = $row.is(".topic-closed");
				
				bindRowEvents($row);
				if (!closed) {
					bindRowMessages($row);
				}
			});
			refreshTopicsDisplay();
			callback && callback.call(null);
		});
	}
	
	var List = {
		initialize: function() {
			Messenger.on("/topics", "TopicAdd", function(message) {
				var topic = message.topic,
					html = renderRow(topic),
					$row;
				
				logger.debug("Messenger.on(/topics/TopicAdd): ", message);
				
				$("#topics-container .topics").prepend(html);
				$row = $("#topics-container .topic:first");
				
				bindRowEvents($row);
				if (!topic.closed) {
					bindRowMessages($row);
				}
				
				refreshTopicsDisplay();
			}).on("/topics", "TopicClose", function(message) {
				var topic = message.topic,
					rowSelector = ".topics li[data-id=" + topic.id + "]",
					$row = $(rowSelector);
					unbindRowMessages($row);
					refreshTopicsDisplay();
			}).on("/topics", "TopicDelete", function(message) {
				var topic = message.topic,
					rowSelector = ".topics li[data-id=" + topic.id + "]",
					$row = $(rowSelector);
					$row.remove();
					refreshTopicsDisplay();
			}).on("/topics", "TopicUpdate", function(message) {
				var topic = message.topic,
					html = renderRow(topic),
					rowSelector = ".topics li[data-id=" + topic.id + "]",
					$row = $(rowSelector);
				
				$row.replaceWith(html);
				bindRowEvents($(rowSelector));
				refreshTopicsDisplay();
				// message events are already bound
			}).on("/topics", "TopicClean", function(message) {
				$(".topics li:has(.delb)").remove();
				refreshTopicsDisplay();
			});
			
			$(document.body).on("click", ".topics .delb", function(e) {
				var $a = $(e.target),
					$li = $a.closest("li"),
					id = $li.data("id");
				
				$.post($.pageRoot + "topic/delete.do", {id: id});
			});
			
			$(document.body).on("click", "#clean", function(e) {
				$.post($.pageRoot + "topic/clean.do");
			});

			$(document.body).on("click mounsedown focus", function(e) {
				var $target = $(e.target);
				if ($("#topics-container").is(":visible")
						&& !($target.is("#topics-count") || $target.parents("#topics-count").size() > 0
							|| $target.is("#topics-container") || $target.parents("#topics-container").size() > 0)) {
					$("#topics-container").hide();
				}
			});
			
			$(document.body).on("click", "#topics-count", function(e) {
				$("#topics-container").toggle();
			});
			
			this.refresh();
		},
		refresh: refreshTopics
	}
	
	window.Topic = {
		List: List,
		register: registerTopicDefinition
	};
	window.TopicTruncate = function(string){
		if (string.length <= 20){
			return string;
		}else{
			return string.substring(0, 19) + "...";
		}
	};
})(this, jQuery, I18N, Logger, Lang, window.Common || {}, Messenger, $.atmosphere || window.atmosphere, _);