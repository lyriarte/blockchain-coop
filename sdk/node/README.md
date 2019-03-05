# blockchain-coop node.js SDK

  *  Installation

```
npm install blockchain-coop
```

  *  Configuration example

Use the [blockchain-coop/bclan](https://github.com/civis-blockchain/blockchain-coop/tree/master/bclan) example to deploy a local blockchain.
The node.js SDK specifics are in [config.json](https://github.com/civis-blockchain/blockchain-coop/blob/master/bclan/config.json).

  *  Command Line Interface

```
./node_modules/blockchain-coop/bcc-cli.js
Usage:
	bcc-cli.js <register|enroll|check|invoke|query> [args]* 
```

```
./node_modules/blockchain-coop/bcc-cli.js register
Usage:
	bcc-cli.js register <user> <password> <org> <newuser> <newpass>
```

```
./node_modules/blockchain-coop/bcc-cli.js enroll
Usage:
	bcc-cli.js enroll <user> <password> <org>
```

```
./node_modules/blockchain-coop/bcc-cli.js check
Usage:
	bcc-cli.js check <user>
```

```
./node_modules/blockchain-coop/bcc-cli.js invoke
Usage:
	bcc-cli.js invoke <user> <endorsers> <channel> <ccid> <fcn> [transaction args]*
endorsers list format: peer0:org0,peer1:org0,peerx:orgx...
```

```
./node_modules/blockchain-coop/bcc-cli.js query
Usage:
	bcc-cli.js query <user> <peer> <org> <channel> <ccid> <fcn> [query args]*
```

  *  HFC keystore cleanup

```
rm -rf ~/.hfc-key-store /tmp/hfc/
```
