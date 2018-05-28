;
(function(window, $, I18N, Logger, Lang, _, Topic, topicTypes, undefined) {
	"use strict"

	var logger = new Logger() || $.logger || Common.logger || new Logger(),
		heredoc = Lang.heredoc,
		loadScript = Lang.loadScript;
	
	var requests = _.map(topicTypes, function(file) {
		var dfd = new $.Deferred();
		loadScript(file, function() {
			dfd.resolve();
		});
		return dfd.promise();
	});
	
	$.when.apply($, requests).done(function() {
		$(function() {
			Topic.List.initialize();
		});
	});
	
})(this, jQuery, I18N, Logger, Lang, _, Topic,
[
"/js/virtual_machine/topics.js",
"/js/disk/topics.js",
"/js/cloud_net/topics.js",
"/js/cloud_node/topics.js",
"/js/template/topics.js",
"/js/affinityGroup/topics.js",
"/js/statellite/topics.js",
"/js/private_net/topics.js",
"/js/firewallnat/topics.js",
"/js/firewallip/topics.js"
]);