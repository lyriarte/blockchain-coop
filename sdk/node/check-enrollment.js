#!/usr/bin/env node


/*
 * Build a context object from args
 */

var args = ["user"];

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


/*
 * Async globals
 */

var client = new hfc();

var errHandler = function(err) {
	console.log(err.stack ? err.stack : err);
	process.exit(1);
};


/*
 * User enrollment check
 */

// Create a keyVal store
hfc.newDefaultKeyValueStore({path: '/tmp/hfc'})
	.then(function(kvs) {
		client.setStateStore(kvs);
		return client.getUserContext(context.user, true);
	},
	errHandler
// Check user enrollment
	).then(function(userCtx) {
		if (userCtx) {
			console.log("enrolled: " + userCtx.isEnrolled());
		}
		else
			console.log("Unknown user: " + context.user);
	},
	errHandler);
