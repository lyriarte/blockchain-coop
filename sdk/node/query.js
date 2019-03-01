#!/usr/bin/env node


/*
 * Build a context object from args
 */

var args = ["user", "peer", "channel", "ccid", "fcn"];

function usage() {
	var msg = "Usage:\n\t" + process.argv[1].split("/").pop();
	args.map((x) => {msg += " <" + x + ">";});
	msg += " [query args]* ";
	msg += "\npeer spec format: peer:org";
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
var peerOrgArray = context.peer.split(':');
context['peer'] = peerOrgArray[0];
context['org'] = peerOrgArray[1];


/*
 * Node.js imports
 */

var fs = require('fs');


/*
 * Hyperledger fabric-sdk-node imports
 */

var hfc = require('fabric-client');




/*
 * Network configuration
 */

hfc.addConfigFile('./config.json');
var ORGS = hfc.getConfigSetting('network');


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
		return new Promise(function(resolve, reject) {
			if (userCtx && userCtx.isEnrolled()) {
				resolve(userCtx);
			}
			else
				reject("Unknown user: " + context.user);
		});
	},
	errHandler
// User enrolled
	).then(function(userCtx) {
		var channel = client.newChannel(context.channel);
		var tls_cacertsBuf = fs.readFileSync(ORGS[context.org][context.peer].tls_cacerts);
		var peer = client.newPeer(
			ORGS[context.org][context.peer].requests,
			{
				'pem': Buffer.from(tls_cacertsBuf).toString(),
				'ssl-target-name-override': ORGS[context.org][context.peer]['server-hostname']
			}
		);
		channel.addPeer(peer);
		var req = {
			chaincodeId: context.ccid,
			txId: null,
			fcn: context.fcn,
			args: process.argv.slice(index)
		};
		return channel.queryByChaincode(req);
	},
	errHandler
// Got query results
	).then(function(payloads) {
		payloads.map((payload) => {console.log(Buffer.from(payload).toString());});
	},
	errHandler);
