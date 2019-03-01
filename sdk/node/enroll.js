#!/usr/bin/env node


/*
 * Build a context object from args
 */

var args = ["user", "password", "org"];

function usage() {
	var msg = "Usage:\n\t" + process.argv[1].split("/").pop();
	args.map((x) => {msg += " <" + x + ">";});
	console.log(msg)
}

if (process.argv.length < args.length + 2) {
	usage();
	process.exit(1);
}

var index = 2;

var context = {
};

args.map((x) => {context[x] = process.argv[index++];});


/*
 * Hyperledger fabric-sdk-node imports
 */

var hfc = require('fabric-client');
var FabricCAServices = require('fabric-ca-client/lib/FabricCAServices.js');
var User = require('fabric-ca-client/lib/User.js');


/*
 * Network configuration
 */

hfc.addConfigFile('./config.json');
var ORGS = hfc.getConfigSetting('network');
var fabricCAEndpoint = ORGS[context.org].ca.url;

var	tlsOptions = {
	trustedRoots: [],
	verify: false
};


/*
 * Async globals
 */

var user = new User(context.user);
var client = new hfc();

var req = {
	enrollmentID: context.user,
	enrollmentSecret: context.password
};

var errHandler = function(err) {
	console.log(err.stack ? err.stack : err);
	process.exit(1);
};


/*
 * User enrollment process
 */

// Create a keyVal store
FabricCAServices.newDefaultKeyValueStore({path: '/tmp/hfc'})
	.then(function(kvs) {
		client.setStateStore(kvs);
		var caService = new FabricCAServices(fabricCAEndpoint, tlsOptions, ORGS[context.org].ca.name);
		return caService.enroll(req);
	},
	errHandler
// Enroll the user
	).then(function(enrollment) {
		console.log("User: '" + context.user + "' enrollment successful");
		return user.setEnrollment(enrollment.key, enrollment.certificate, ORGS[context.org].mspid);
	},
	errHandler
// Store enrolled user info
	).then(function() {
		var userInfoStr = user.toString();
		console.log("signing identity: '" + JSON.parse(userInfoStr).enrollment.signingIdentity + "'");
//		return client.setUserContext(user,false); #### TODO FIX Client.js:1490 user instanceof User
		client._userContext = user;
		return client.saveUserToStateStore();
	},
	errHandler
	).then(function() {
		console.log("User: '" + context.user + "' saved to state store");
	},
	errHandler);
