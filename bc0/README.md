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
configtxgen -profile ThingagoraBC0OrdererGenesis  -channelID bczero -outputBlock ./config/genesis.block
```

#### Channels

```
export CHANNEL_NAME=sandbox
```

  * Channel configuration transaction 

```
configtxgen -profile BlockchainCoopBC0PeerChannels -outputCreateChannelTx ./config/${CHANNEL_NAME}.tx -channelID $CHANNEL_NAME
```

  * Anchor peer transaction

```
configtxgen -profile BlockchainCoopBC0PeerChannels -outputAnchorPeersUpdate ./config/BlockchainCoopBC0PeerMSPanchors.tx -channelID ${CHANNEL_NAME} -asOrg ThingagoraBC0Peer
```

## Initial deployment

### Docker containers environment

```
export ORGA=pr-bc0.thingagora.org

echo COMPOSE_PROJECT_NAME="bc0" > .env
echo ca__CA_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/${ORGA}/ca/*_sk)) >> .env
echo ca__TLS_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/${ORGA}/tlsca/*_sk)) >> .env
echo ca__ADMIN=$(cat /dev/urandom | xxd | head -n 1 | cut -b 10-49 | sed "s/ //g") >> .env
echo ca__PASSWD=$(cat /dev/urandom | xxd | head -n 1 | cut -b 10-49 | sed "s/ //g") >> .env
```

Note that the `.env` file contains the CA admin and password, remove it from remote hosts and keep the credentials safe.

### Remote hosts setup

Run the following commands as root to create the blockchain user.

```
apt-get install docker.io docker-compose
adduser blockchain
adduser blockchain docker
```

### Create archives for remote hosts

```
orderer-archive.sh orderer0 or-bc0.thingagora.org
scp orderer_orderer0_or-bc0.thingagora.org.tgz blockchain@orderer0.or-bc0.thingagora.org:
```

```
ca-archive.sh pr-bc0.thingagora.org
scp ca_pr-bc0.thingagora.org.tgz blockchain@ca.pr-bc0.thingagora.org:
```

```
peer-archive.sh peer0 pr-bc0.thingagora.org
scp peer_peer0_pr-bc0.thingagora.org.tgz blockchain@peer0.pr-bc0.thingagora.org:
```

### Start containers on remote hosts

```
ssh blockchain@orderer0.or-bc0.thingagora.org
tar xvzf orderer_orderer0_or-bc0.thingagora.org.tgz
docker-compose -f docker-compose.yaml up -d orderer0.or-bc0.thingagora.org
```

```
ssh blockchain@ca.pr-bc0.thingagora.org
tar xvzf ca_pr-bc0.thingagora.org.tgz
docker-compose -f docker-compose.yaml up -d ca.pr-bc0.thingagora.org
```

```
ssh blockchain@peer0.pr-bc0.thingagora.org
tar xvzf peer_peer0_pr-bc0.thingagora.org.tgz
docker-compose -f docker-compose.yaml up -d peer0.pr-bc0.thingagora.org
```

## Network setup on a local machine

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
peer channel create -o orderer0.or-bc0.thingagora.org:7050 -c ${CHANNEL_NAME} -f /etc/hyperledger/config/${CHANNEL_NAME}.tx --tls --cafile /etc/hyperledger/orderer/tlsca.or-bc0.thingagora.org-cert.pem
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



