#!/usr/bin/env node


/*
 * Build a context object from args
 */

var args = ["user", "endorsers", "channel", "ccid", "fcn"];

function usage() {
	var msg = "Usage:\n\t" + process.argv[1].split("/").pop();
	args.map((x) => {msg += " <" + x + ">";});
	msg += " [transaction args]* ";
	msg += "\nendorsers list format: peer0:org0,peer1:org0,peerx:orgx...";
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
var peersOrgs = context.endorsers.split(',');
context.endorsers = [];
peersOrgs.map(function(peerOrgStr)
{
	var peerOrgArray = peerOrgStr.split(':');
	context.endorsers.push(
	{
		'peer': peerOrgArray[0], 
		'org': peerOrgArray[1]
	});
});


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
var channel;

var errHandler = function(err) {
	console.log(err.stack ? err.stack : err);
	process.exit(1);
};


/*
 * Peer info management utilities
 */

var peerInfoSetProxy = function(hfcClient, networkConfig, peerInfo) {
	var tls_cacertsBuf = fs.readFileSync(networkConfig[peerInfo.org][peerInfo.peer].tls_cacerts);
	peerInfo.peerProxy = hfcClient.newPeer(
		networkConfig[peerInfo.org][peerInfo.peer].requests,
		{
			'pem': Buffer.from(tls_cacertsBuf).toString(),
			'ssl-target-name-override': networkConfig[peerInfo.org][peerInfo.peer]['server-hostname']
		}
	);
}


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
		channel = client.newChannel(context.channel);
		var tls_cacertsBuf = fs.readFileSync(ORGS.orderer.tls_cacerts);
		var orderer = client.newOrderer(
			ORGS.orderer.url, 
			{
				'pem': Buffer.from(tls_cacertsBuf).toString(),
				'ssl-target-name-override': ORGS.orderer['server-hostname']
			}
		);
		channel.addOrderer(orderer);
		var targets = [];
		context.endorsers.map(function(endorser)
		{
			peerInfoSetProxy(client, ORGS, endorser);
			channel.addPeer(endorser.peerProxy);
			targets.push(endorser.peerProxy);
		});
		var req = {
			chaincodeId: context.ccid,
			txId: client.newTransactionID(),
			fcn: context.fcn,
			targets: targets,
			args: process.argv.slice(index)
		};
		console.log("Transaction ID: " + req.txId.getTransactionID());
		return channel.sendTransactionProposal(req);
	},
	errHandler
// Got endorsers results
	).then(function(results) {
		messages = "";
		results[0].map((result) => {messages += (result.response && result.response.status == 200 ? result.response.message : "FAIL" ) + ", ";});	
		console.log("Endorsers results: " + messages.substring(0,messages.length-2));
		var req = {
			proposalResponses: results[0],
			proposal: results[1]
		};
		return channel.sendTransaction(req);
	},
	errHandler
// Got orderer response
	).then(function(response) {
		console.log("Orderer response: " + response.status);
	},
	errHandler);
