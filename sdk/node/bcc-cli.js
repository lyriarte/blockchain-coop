#!/usr/bin/env node


/*
 * Build a context object from args
 */

var args = {
	"register": ["user", "password", "org", "newuser", "newpass"], 
	"enroll": ["user", "password", "org"], 
	"check": ["user"], 
	"invoke": ["user", "endorsers", "channel", "ccid", "fcn"], 
	"query": ["user", "peer", "org", "channel", "ccid", "fcn"]
};
var cmds = Object.keys(args);

var command = process.argv[2];

function usage() {
	var msg = "Usage:\n\t" + process.argv[1].split("/").pop();
	if (cmds.indexOf(command) < 0) {
		msg += " <" + cmds.shift();
		cmds.map((x) => {msg += "|" + x;});
		msg += "> [args]* ";
	}
	else {
		msg += " " + command;
		args[command].map((x) => {msg += " <" + x + ">";});
		if (command == "query")
			msg += " [query args]*";
		else if (command == "invoke")
			msg += " [transaction args]*\nendorsers list format: peer0:org0,peer1:org0,peerx:orgx...";
	}
	console.log(msg);
}

if (cmds.indexOf(command) < 0 || process.argv.length < args[command].length + 3) {
	usage(command);
	process.exit(1);
}

var index = 3;

var context = {
};

args[command].map((x) => {context[x] = process.argv[index++];});

if (command == "invoke") {
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
}

if (command == "query" || command == "invoke")
	context["args"] = process.argv.slice(args[command].length + 3);

/*
 * Create a BlockchainCoop instance
 */

var BlockchainCoop = require("./blockchain-coop.js").BlockchainCoop;
var bcc = new BlockchainCoop(context);


/*
 * Perform the command
 */

var errHandler = function(err) {
	console.log(err.stack ? err.stack : err);
	process.exit(1);
};

var successHandler = function(msg) {
	console.log(msg);
};


var cbctx = bcc.perform(command, context, successHandler, errHandler);


