/**
 * The BlockchainCoop wrapper object for Hyperledger fabric and fabric-ca client
 * 
 * @constructor
 * @param config {string} - Hyperledger fabric client config file.
 */
var BlockchainCoop = function(config) {
	this.hfc = require('fabric-client');
	this.FabricCAServices = require('fabric-ca-client/lib/FabricCAServices.js');
	this.User = require('fabric-ca-client/lib/User.js');
	this.hfc.addConfigFile('./config.json');
	this.ORGS = this.hfc.getConfigSetting('network');
	this.client = new this.hfc();
	this.kvsPath = '/tmp/hfc';
	return this;
};
exports.BlockchainCoop = BlockchainCoop;



/**
 * The perform method makes the BlockchainCoop wrapper object perform a hfc / ca command.
 * 
 * @param command {string} - The command to execute.
 * @param context {object} - The context object with the command parameters.
 * @param onOk {function} - Success callback, called with one result object for parameter.
 * @param onError {function} - Error callback, called with one error object for parameter.
 * @return {object} cbctx - The object in the context of which the callbacks are executed.
 */
BlockchainCoop.prototype.perform = function(command, context, onOk, onError) {
	var cbctx = {
		blockchain: this,
		command: command,
		context: context,
		onOk: onOk ? onOk: function() {},
		onError: onError ? onError: function() {}
	};

	return cbctx;
};
