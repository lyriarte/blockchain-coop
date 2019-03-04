# Network configuration: bclan


## Initial configuration


### Cryptography

```
cryptogen generate --config=./crypto-config.yaml
```

### Artefacts

```
export FABRIC_CFG_PATH=$PWD
```

#### Orderers

  * Orderer genesis block

```
configtxgen -profile BlockchainLANOrdererGenesis -outputBlock ./config/genesis.block
```

#### Channels

```
export CHANNEL=sandbox
```

  * Channel configuration transaction 

```
configtxgen -profile BlockchainLANCoopChannels -outputCreateChannelTx ./config/${CHANNEL}.tx -channelID $CHANNEL
```

  * Anchor peer transaction

```
configtxgen -profile BlockchainLANCoopChannels -outputAnchorPeersUpdate ./config/BlockchainLANCoopMSPanchors.tx -channelID $CHANNEL -asOrg BlockchainLANCoop
```

## Network setup

### Docker containers environment

```
export ORGA=bc-coop.bclan

echo COMPOSE_PROJECT_NAME="bclan" > .env
echo ca__CA_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/${ORGA}/ca/*_sk)) >> .env
echo ca__TLS_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/${ORGA}/tlsca/*_sk)) >> .env
echo ca__ADMIN=$(cat /dev/urandom | xxd | head -n 1 | cut -b 10-49 | sed "s/ //g") >> .env
echo ca__PASSWD=$(cat /dev/urandom | xxd | head -n 1 | cut -b 10-49 | sed "s/ //g") >> .env

echo cli_ORGA=${ORGA} >> .env
echo cli_USER=Admin >> .env
```

### Start network

```
docker-compose -f docker-compose.yaml up -d
```

### Use the CLI container environment


  * Runtime CLI configuration

```
cli_ORGA=bc-coop.bclan
echo cli_ORGA=${cli_ORGA} > ../util/env

# orderer address and certificate
echo ORDERER_ADDR="orderer.bclan:7050" >> ../util/env
echo ORDERER_CERT="/etc/hyperledger/orderer/tlsca.bclan-cert.pem" >> ../util/env

# current session chaincode
echo CHANNEL="sandbox" >> ../util/env
echo CHAINCODE="ex02" >> ../util/env

# override target peer if needed
echo CORE_PEER_ADDRESS="peer0.${cli_ORGA}:7051" >> ../util/env

# Enter CLI environment
docker exec -it cli-bclan /bin/bash
```

```
# Channel creation in CLI environment
source /opt/blockchain-coop/env
peer channel create -o ${ORDERER_ADDR} -c ${CHANNEL} -f /etc/hyperledger/config/${CHANNEL}.tx --tls --cafile ${ORDERER_CERT}
peer channel join -b ${CHANNEL}.block
```

```
# Chaincode intantiation in CLI environment
source /opt/blockchain-coop/env
peer chaincode install -n ${CHAINCODE} -v 1.0 -p blockchain-coop/go/chaincode_example02/
peer chaincode instantiate -o ${ORDERER_ADDR} --tls --cafile ${ORDERER_CERT} -C ${CHANNEL} -n ${CHAINCODE} -v 1.0 -c '{"Args":["init","a", "100", "b","200"]}' -P "OR ('BlockchainLANCoopMSP.member')"
```

```
# Chaincode usage in CLI environment
source /opt/blockchain-coop/env
peer chaincode query -C ${CHANNEL} -n ${CHAINCODE} -c '{"Args":["query","a"]}'
peer chaincode invoke -o ${ORDERER_ADDR} -C ${CHANNEL} -n ${CHAINCODE} --tls --cafile ${ORDERER_CERT} -c '{"Args":["invoke","a","b","10"]}'
peer chaincode query -C ${CHANNEL} -n ${CHAINCODE} -c '{"Args":["query","b"]}'
```

```
exit
```


### Use the node.js SDK

```
export PATH=../sdk/node:$PATH
source .env
```

  * Enroll the bclan admin

```
bcc-cli.js enroll $ca__ADMIN $ca__PASSWD bclan
bcc-cli.js check $ca__ADMIN
bcc-cli.js query $ca__ADMIN peer0 bclan sandbox ex02 query a
bcc-cli.js invoke $ca__ADMIN peer0:bclan sandbox ex02 invoke a b 1
bcc-cli.js query $ca__ADMIN peer0 bclan sandbox ex02 query a
```

  * Register and enroll a new user

```
bcc-cli.js register $ca__ADMIN $ca__PASSWD bclan Fred ABCD1234
bcc-cli.js enroll Fred ABCD1234 bclan
bcc-cli.js check Fred
bcc-cli.js query Fred peer0 bclan sandbox ex02 query b
bcc-cli.js invoke Fred peer0:bclan sandbox ex02 invoke a b 50
bcc-cli.js query Fred peer0 bclan sandbox ex02 query b
```


### Stop network and cleanup

```
docker-compose -f docker-compose.yaml down
```

```
git clean -fdx
```

