/**
 * The BlockchainCoop wrapper object for Hyperledger fabric and fabric-ca client
 * 
 * @constructor
 * @param config {string} - Hyperledger fabric client config file.
 */
var BlockchainCoop = function(config) {
	this.fs = require('fs');
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
	else if (command == "register")
		cbctx = this.register(context.user, context.password, context.org, context.newuser, context.newpass, cbctx);
	else if (command == "query")
		cbctx = this.query(context.user, context.peer, context.org, context.channel, context.ccid, context.fcn, context.args, cbctx);
	else if (command == "invoke")
		cbctx = this.invoke(context.user, context.endorsers, context.channel, context.ccid, context.fcn, context.args, cbctx);

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
	var hfcOrg = self.ORGS.organisations[org];
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


/**
 * User registration
 * 
 * @param user {string} - The user name.
 * @param password {string} - The user password.
 * @param org {string} - The organization.
 * @param newuser {string} - The new user name.
 * @param newpass {string} - The new user password.
 * @param cbctx {object} - Object in the context of which the callbacks are executed.
 * @return {object} cbctx - The context object.
 */

BlockchainCoop.prototype.register = function(user, password, org, newuser, newpass, cbctx) {
	var self = this;
	var hfcUser = new self.User(user);
	var hfcOrg = self.ORGS.organisations[org];
	var req = {
		enrollmentID: user,
		enrollmentSecret: password
	};
	var	tlsOptions = {
		trustedRoots: [],
		verify: false
	};
	var caService = new self.FabricCAServices(hfcOrg.ca.url, tlsOptions, hfcOrg.ca.name);
	caService.enroll(req)
// Enroll the user
	.then(function(enrollment) {
		return hfcUser.setEnrollment(enrollment.key, enrollment.certificate, hfcOrg.mspid);
	},
	cbctx.onError
// Register new user
	).then(function() {
		return caService.register({enrollmentID: newuser, enrollmentSecret: newpass, affiliation: 'org1.department1', role: 'client'}, hfcUser);
	},
	cbctx.onError
// Verify new user password
	).then(function(newpass) {
		cbctx.onOk({user: newuser, password: newpass});
	},
	cbctx.onError);

	return cbctx;
};


/**
 * Chaincode query
 * 
 * @param user {string} - The user name.
 * @param peer {string} - The peer to query.
 * @param org {string} - The organization.
 * @param channel {string} - The channel name.
 * @param ccid {string} - The chaincode id.
 * @param fcn {string} - The query function name.
 * @param args {array} - The query arguments array.
 * @param cbctx {object} - Object in the context of which the callbacks are executed.
 * @return {object} cbctx - The context object.
 */

BlockchainCoop.prototype.query = function(user, peer, org, channel, ccid, fcn, args, cbctx) {
	var self = this;
	var hfcOrg = self.ORGS.organisations[org];
	// Create a keyVal store
	self.hfc.newDefaultKeyValueStore({path: self.kvsPath})
		.then(function(kvs) {
			self.client.setStateStore(kvs);
			return self.client.getUserContext(user, true);
		},
		cbctx.onError
	// Check user enrollment
	).then(function(userCtx) {
		return new Promise(function(resolve, reject) {
			if (userCtx && userCtx.isEnrolled()) {
				resolve(userCtx);
			}
			else
				reject("Unknown user: " + user);
		});
	},
	cbctx.onError
// User enrolled
	).then(function(userCtx) {
		var hfcChannel = self.client.newChannel(channel);
		var tlsCacertsBuf = self.fs.readFileSync(hfcOrg.peers[peer].tlsCacerts);
		var hfcPeer = self.client.newPeer(
			hfcOrg.peers[peer].requests,
			{
				'pem': Buffer.from(tlsCacertsBuf).toString(),
				'ssl-target-name-override': hfcOrg.peers[peer]['serverHostname']
			}
		);
		hfcChannel.addPeer(hfcPeer);
		var req = {
			chaincodeId: ccid,
			txId: null,
			fcn: fcn,
			args: Array.isArray(args) ? args : [args]
		};
		return hfcChannel.queryByChaincode(req);
	},
	cbctx.onError
// Got query results
	).then(function(payloads) {
		results = [];
		function addPayload(payload) {
			var str = null;
			if (Buffer.isBuffer(payload))
				str = Buffer.from(payload).toString();
			else if (payload.toString)
				str = payload.toString();
			if (str) {
				if (str.indexOf("Error:") == 0)
					cbctx.onError(str);
				else {
					try {
						obj = JSON.parse(str);
						results.push(obj);
					}
					catch (error) {
						results.push(str);
					}
				}
			}
		}
		payloads.map(addPayload);
		var result = results;
		if (result.length == 0)
			result = null;
		else if (result.length == 1)
			result = results.pop()
		cbctx.onOk(result);
	},
	function(error) {
		if (error && error.toString())
			cbctx.onError(error.toString());
		else
			cbctx.onError("Error: query failure");
	});

	return cbctx;
};



/**
 * Chaincode invocation
 * 
 * @param user {string} - The user name.
 * @param endorsers {string} - The endorser peers.
 * @param channel {string} - The channel name.
 * @param ccid {string} - The chaincode id.
 * @param fcn {string} - The function to invoke.
 * @param args {array} - The function arguments array.
 * @param cbctx {object} - Object in the context of which the callbacks are executed.
 * @return {object} cbctx - The context object.
 */

BlockchainCoop.prototype.invoke = function(user, endorsers, channel, ccid, fcn, args, cbctx) {
	var self = this;
	var hfcChannel = null;

	// Peer info management utility
	var peerInfoSetProxy = function(peerInfo) {
		var tlsCacertsBuf = self.fs.readFileSync(self.ORGS.organisations[peerInfo.org].peers[peerInfo.peer].tlsCacerts);
		peerInfo.peerProxy = self.client.newPeer(
			self.ORGS.organisations[peerInfo.org].peers[peerInfo.peer].requests,
			{
				'pem': Buffer.from(tlsCacertsBuf).toString(),
				'ssl-target-name-override': self.ORGS.organisations[peerInfo.org].peers[peerInfo.peer]['serverHostname']
			}
		);
	}

	// Create a keyVal store
	self.hfc.newDefaultKeyValueStore({path: self.kvsPath})
		.then(function(kvs) {
			self.client.setStateStore(kvs);
			return self.client.getUserContext(user, true);
		},
		cbctx.onError
	// Check user enrollment
	).then(function(userCtx) {
		return new Promise(function(resolve, reject) {
			if (userCtx && userCtx.isEnrolled()) {
				resolve(userCtx);
			}
			else
				reject("Unknown user: " + user);
		});
	},
	cbctx.onError
// User enrolled
	).then(function(userCtx) {
		hfcChannel = self.client.newChannel(channel);
		var tlsCacertsBuf = self.fs.readFileSync(self.ORGS.orderer.tlsCacerts);
		var orderer = self.client.newOrderer(
			self.ORGS.orderer.url, 
			{
				'pem': Buffer.from(tlsCacertsBuf).toString(),
				'ssl-target-name-override': self.ORGS.orderer['serverHostname']
			}
		);
		hfcChannel.addOrderer(orderer);
		var targets = [];
		endorsers.map(function(endorser)
		{
			peerInfoSetProxy(endorser);
			hfcChannel.addPeer(endorser.peerProxy);
			targets.push(endorser.peerProxy);
		});
		var req = {
			chaincodeId: ccid,
			txId: self.client.newTransactionID(),
			fcn: fcn,
			targets: targets,
			args: Array.isArray(args) ? args : [args]
		};
		cbctx['transactionID'] = req.txId.getTransactionID();
		return hfcChannel.sendTransactionProposal(req);
	},
	cbctx.onError
// Got endorsers results
	).then(function(results) {
		cbctx['proposalResponses'] = results[0];
		var req = {
			proposalResponses: results[0],
			proposal: results[1]
		};
		return hfcChannel.sendTransaction(req);
	},
	cbctx.onError
// Got orderer response
	).then(function(response) {
		cbctx.onOk(response);
	},
	function(error) {
		if (error && error.toString())
			cbctx.onError(error.toString());
		else
			cbctx.onError("Error: invoke failure");
	});

	return cbctx;
};


