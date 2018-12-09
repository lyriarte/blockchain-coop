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
export CHANNEL_NAME=sandbox
```

  * Channel configuration transaction 

```
configtxgen -profile BlockchainLANCoopChannels -outputCreateChannelTx ./config/channel.tx -channelID $CHANNEL_NAME
```

  * Anchor peer transaction

```
configtxgen -profile BlockchainLANCoopChannels -outputAnchorPeersUpdate ./config/BlockchainLANCoopMSPanchors.tx -channelID $CHANNEL_NAME -asOrg BlockchainLANCoop
```

## Network setup

### Docker containers environment

```
echo COMPOSE_PROJECT_NAME="bclan" > .env
echo ca__CA_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/bc-coop.bclan/ca/*_sk)) >> .env
echo ca__TLS_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/bc-coop.bclan/tlsca/*_sk)) >> .env
echo ca__ADMIN=CHANGE_ME >> .env
echo ca__PASSWD=CHANGE_ME >> .env
```

### Start network

```
docker-compose -f docker-compose.yaml up -d
```

### Use the CLI container environment

```
docker exec -it cli-peer0 /bin/bash
```

```
export CHANNEL_NAME=sandbox
peer channel create -o orderer.bclan:7050 -c ${CHANNEL_NAME} -f /etc/hyperledger/config/channel.tx --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem
peer channel join -b ${CHANNEL_NAME}.block
```

```
export CHANNEL_NAME=sandbox
export CHAINCODE=ex02
peer chaincode install -n ${CHAINCODE} -v 1.0 -p blockchain-coop/chaincode_example02/go/
peer chaincode instantiate -o orderer.bclan:7050 --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -C ${CHANNEL_NAME} -n ${CHAINCODE} -v 1.0 -c '{"Args":["init","a", "100", "b","200"]}' -P "OR ('BlockchainLANCoopMSP.member')"
```

```
export CHANNEL_NAME=sandbox
export CHAINCODE=ex02
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["query","a"]}'
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["invoke","a","b","10"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["query","b"]}'
```

```
exit
```

### Stop network and cleanup

```
docker-compose -f docker-compose.yaml down
```

```
git clean -fdx
```



