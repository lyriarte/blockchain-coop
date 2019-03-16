#!/usr/bin/env node


/*
 * Build a context object from args
 */

var args = ["port", "user", "endorsers", "channel", "ccid"];

function usage() {
	var msg = "Usage:\n\t" + process.argv[1].split("/").pop();
	args.map((x) => {msg += " <" + x + ">";});
	msg += "\nendorders list format: peer0:org0,peer1:org0,peerx:orgx...";
	msg += "\nfirst endorder peer:org pair is used for queries.";
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
// Build context endorsers for invoke commands
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
// Build context peer/org for query commands
context.peer = context.endorsers[0].peer;
context.org = context.endorsers[0].org;


/*
 * BlockchainCoop command callbacks
 */

var errHandler = function(err) {
	console.log(err.stack ? err.stack : err);
	result = err.toString ? err.toString() : "Error: blockchain call failure";
	httpResHandler(this.res, result);
};

var successHandler = function(msg) {
	httpResHandler(this.res, msg);
};

/*
 * Create a BlockchainCoop instance
 */

var BlockchainCoop = require("./blockchain-coop.js").BlockchainCoop;


/*
 * HTTP requests handlers
 */

function argsFromQueryString(queryString) {
	var args = {};
	if (!queryString)
		return args;
	var getVars = decodeURI(queryString).split("\&");
	for (var i=0; i<getVars.length; i++) {
		var varVal = getVars[i].split("\=");
		if (varVal.length == 2)
			args[varVal[0]] = varVal[1];
	}
	return args;
}

var httpResHandler = function(res, result) {
	res.writeHead(200, {
		'Content-Type': 'application/json', 
		'Access-Control-Allow-Origin': '*'}
	);
	str = null;
	try {
		str = JSON.stringify(result);
	}
	catch (error) {
		str = result;
	}
	res.write(str);
	res.end();
}

var httpReqHandler = function(req, res) {
	var result = "Error: generic error";
	var reqArgs = url.parse(req.url, true).query;
	var command = reqArgs.cmd;
	context.fcn = reqArgs.fcn;
	context.args = reqArgs.args;
	try {
		var cbctx = {};
		var bcc = new BlockchainCoop(context);
		cbctx = bcc.perform(command, context, successHandler, errHandler);
		cbctx.res = res;
	}
	catch(err) {
		errHandler(err);
	}
}


/*
 * Create HTTP server
 */

var http = require('http');
var url = require('url');

var srvr = http.createServer(httpReqHandler)
http.createServer(httpReqHandler)
console.log("Starting REST server on port " + context.port);

srvr.listen(context.port);

