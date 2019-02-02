# Network configuration: bc0


Blockchain sandbox bc0 for cooperating organizations:

  * thingagora.org

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
configtxgen -profile ThingagoraBC0OrdererGenesis -outputBlock ./config/genesis.block
```

#### Channels

```
export CHANNEL_NAME=sandbox
```

  * Channel configuration transaction 

```
configtxgen -profile BlockchainCoopBC0PeerChannels -outputCreateChannelTx ./config/channel.tx -channelID $CHANNEL_NAME
```

  * Anchor peer transaction

```
configtxgen -profile BlockchainCoopBC0PeerChannels -outputAnchorPeersUpdate ./config/BlockchainCoopBC0PeerMSPanchors.tx -channelID $CHANNEL_NAME -asOrg ThingagoraBC0Peer
```

## Network setup

### Docker containers environment

```
echo COMPOSE_PROJECT_NAME="bc0" > .env
echo ca_ThingagoraBC0Peer__CA_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/pr-bc0.thingagora.org/ca/*_sk)) >> .env
echo ca_ThingagoraBC0Peer__TLS_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/pr-bc0.thingagora.org/tlsca/*_sk)) >> .env
echo ca_ThingagoraBC0Peer__ADMIN=CHANGE_ME >> .env
echo ca_ThingagoraBC0Peer__PASSWD=CHANGE_ME >> .env
```

### Start network

```
docker-compose -f docker-compose.yaml up -d
```

### Use the CLI container environment

```
docker exec -it cli-peer0_ThingagoraBC0Peer /bin/bash
```

```
export CHANNEL_NAME=sandbox
peer channel create -o orderer0.or-bc0.thingagora.org:7050 -c ${CHANNEL_NAME} -f /etc/hyperledger/config/channel.tx --tls --cafile /etc/hyperledger/orderer/tlsca.or-bc0.thingagora.org-cert.pem
peer channel join -b ${CHANNEL_NAME}.block
```

```
export CHANNEL_NAME=sandbox
export CHAINCODE=ex02
peer chaincode install -n ${CHAINCODE} -v 1.0 -p blockchain-coop/chaincode_example02/go/
peer chaincode instantiate -o orderer0.or-bc0.thingagora.org:7050 --tls --cafile /etc/hyperledger/orderer/tlsca.or-bc0.thingagora.org-cert.pem -C ${CHANNEL_NAME} -n ${CHAINCODE} -v 1.0 -c '{"Args":["init","a", "100", "b","200"]}' -P "OR ('ThingagoraBC0PeerMSP.member')"
```

```
export CHANNEL_NAME=sandbox
export CHAINCODE=ex02
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["query","a"]}'
peer chaincode invoke -o orderer0.or-bc0.thingagora.org:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.or-bc0.thingagora.org-cert.pem -c '{"Args":["invoke","a","b","10"]}'
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



