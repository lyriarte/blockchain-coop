#!/usr/bin/env node


/*
 * Build a context object from args
 */

var args = ["user", "password", "org", "newuser", "newpass"];

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

var caService = new FabricCAServices(fabricCAEndpoint, tlsOptions, ORGS[context.org].ca.name);

var user = new User(context.user);

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

caService.enroll(req)
// Enroll the user
	.then(function(enrollment) {
		console.log("User: '" + context.user + "' enrollment successful");
		return user.setEnrollment(enrollment.key, enrollment.certificate, ORGS[context.org].mspid);
	},
	errHandler
// Register new user
	).then(function() {
		var userInfoStr = user.toString();
		console.log("signing identity: '" + JSON.parse(userInfoStr).enrollment.signingIdentity + "'");
		return caService.register({enrollmentID: context.newuser, enrollmentSecret: context.newpass, affiliation: 'org1.department1', role: 'client'}, user);
	},
	errHandler
// Verify new user password
	).then(function(newpass) {
		console.log("New user: '" + context.newuser + "' registered with password: '" + newpass + "'");
	},
	errHandler);
