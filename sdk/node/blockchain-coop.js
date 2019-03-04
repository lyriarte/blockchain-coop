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

	if (command == "check")
		cbctx = this.check(context.user, cbctx);
	else if (command == "enroll")
		cbctx = this.enroll(context.user, context.password, context.org, cbctx);

	return cbctx;
};


/**
 * User enrollment check
 * 
 * @param user {string} - The user name.
 * @param cbctx {object} - Object in the context of which the callbacks are executed.
 * @return {object} cbctx - The context object.
 */

BlockchainCoop.prototype.check = function(user, cbctx) {
	var self = this;
	// Create a keyVal store
	self.hfc.newDefaultKeyValueStore({path: self.kvsPath})
		.then(function(kvs) {
			self.client.setStateStore(kvs);
			return self.client.getUserContext(user, true);
		},
		cbctx.onError
	// Check user enrollment
		).then(function(userCtx) {
			if (userCtx) {
				cbctx.onOk(userCtx.isEnrolled());
			}
			else
				cbctx.onError("Unknown user: " + user);
		},
		cbctx.onError);

	return cbctx;
};



/**
 * User enrollment
 * 
 * @param user {string} - The user name.
 * @param password {string} - The user password.
 * @param org {string} - The organization.
 * @param cbctx {object} - Object in the context of which the callbacks are executed.
 * @return {object} cbctx - The context object.
 */

BlockchainCoop.prototype.enroll = function(user, password, org, cbctx) {
	var self = this;
	var hfcUser = new self.User(user);
	var hfcOrg = self.ORGS[org];
	var req = {
		enrollmentID: user,
		enrollmentSecret: password
	};
	var	tlsOptions = {
		trustedRoots: [],
		verify: false
	};
	// Create a keyVal store
	self.hfc.newDefaultKeyValueStore({path: self.kvsPath})
		.then(function(kvs) {
			self.client.setStateStore(kvs);
		var caService = new self.FabricCAServices(hfcOrg.ca.url, tlsOptions, hfcOrg.ca.name);
		return caService.enroll(req);
	},
	cbctx.onError
// Enroll the user
	).then(function(enrollment) {
		return hfcUser.setEnrollment(enrollment.key, enrollment.certificate, hfcOrg.mspid);
	},
	cbctx.onError
// Store enrolled user info
	).then(function() {
//		return client.setUserContext(user,false); #### TODO FIX Client.js:1490 user instanceof User
		self.client._userContext = hfcUser;
		return self.client.saveUserToStateStore();
	},
	cbctx.onError
	).then(function() {
		cbctx.onOk(hfcUser);
	},
	cbctx.onError);

	return cbctx;
};

