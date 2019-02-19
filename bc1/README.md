# Network configuration: bc1


Blockchain sandbox bc1 for cooperating organizations:

  * chain-ops.net
  * civis-blockchain.org
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
configtxgen -profile ChainOpsBC1OrdererGenesis  -channelID bcone -outputBlock ./config/genesis.block
```

#### Channels

```
export CHANNEL=sandbox
```

  * Channel configuration transaction 

```
configtxgen -profile BlockchainCoopBC1PeerChannels -outputCreateChannelTx ./config/${CHANNEL}.tx -channelID $CHANNEL
```

  * Anchor peer transaction

Peer MSP ids: ChainOpsBC1Peer , CivisBlockchainBC1Peer , ThingagoraBC1Peer

```
configtxgen -profile BlockchainCoopBC1PeerChannels -outputAnchorPeersUpdate ./config/ChainOpsBC1PeerMSPanchors.tx -channelID ${CHANNEL} -asOrg ChainOpsBC1Peer
configtxgen -profile BlockchainCoopBC1PeerChannels -outputAnchorPeersUpdate ./config/CivisBlockchainBC1PeerMSPanchors.tx -channelID ${CHANNEL} -asOrg CivisBlockchainBC1Peer
configtxgen -profile BlockchainCoopBC1PeerChannels -outputAnchorPeersUpdate ./config/ThingagoraBC1PeerMSPanchors.tx -channelID ${CHANNEL} -asOrg ThingagoraBC1Peer
```

## Initial deployment

### Docker containers environment

```
export MSP=ChainOpsBC1PeerMSP
export ORGA=pr-bc1.chain-ops.net

echo COMPOSE_PROJECT_NAME="bc1" > .env
echo ca_1_CA_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/pr-bc1.chain-ops.net/ca/*_sk)) >> .env
echo ca_1_TLS_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/pr-bc1.chain-ops.net/tlsca/*_sk)) >> .env
echo ca_2_CA_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/pr-bc1.civis-blockchain.org/ca/*_sk)) >> .env
echo ca_2_TLS_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/pr-bc1.civis-blockchain.org/tlsca/*_sk)) >> .env
echo ca_3_CA_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/pr-bc1.thingagora.org/ca/*_sk)) >> .env
echo ca_3_TLS_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/pr-bc1.thingagora.org/tlsca/*_sk)) >> .env
echo ca__ADMIN=$(cat /dev/urandom | xxd | head -n 1 | cut -b 10-49 | sed "s/ //g") >> .env
echo ca__PASSWD=$(cat /dev/urandom | xxd | head -n 1 | cut -b 10-49 | sed "s/ //g") >> .env

echo cli_MSP=${MSP} >> .env
echo cli_ORGA=${ORGA} >> .env
echo cli_USER=Admin >> .env
```

Note that the `.env` file contains the CA admin and password, remove it from remote hosts and keep the credentials safe.



## Network setup on a local machine

### Start network locally with only one peer per org

```
docker-compose -f docker-compose.yaml up -d
```

### Use the CLI container environment


  * Runtime CLI configuration

Peer MSP ids: ChainOpsBC1Peer , CivisBlockchainBC1Peer , ThingagoraBC1Peer

  * ChainOps

```
cli_MSP=${MSP}
cli_ORGA=${ORGA}
idx=0
echo cli_ORGA=${cli_ORGA} > ../util/env

# orderer address and certificate
echo ORDERER_ADDR="orderer0.or-bc1.chain-ops.net:7050" >> ../util/env
echo ORDERER_CERT="/etc/hyperledger/orderer/tlsca.or-bc1.chain-ops.net-cert.pem" >> ../util/env

# current session chaincode
echo CHANNEL="sandbox" >> ../util/env
echo CHAINCODE="ex02" >> ../util/env

# Enter CLI environment
docker exec -it cli-ChainOpsBC1Orderer /bin/bash
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
peer chaincode instantiate -o ${ORDERER_ADDR} --tls --cafile ${ORDERER_CERT} -C ${CHANNEL} -n ${CHAINCODE} -v 1.0 -c '{"Args":["init","a", "100", "b","200"]}' -P "OR ('ChainOpsBC1PeerMSP.member', 'CivisBlockchainBC1PeerMSP.member', 'ThingagoraBC1PeerMSP.member')"
```

```
# Chaincode usage in CLI environment
source /opt/blockchain-coop/env
peer chaincode query -C ${CHANNEL} -n ${CHAINCODE} -c '{"Args":["query","a"]}'
peer chaincode invoke -o ${ORDERER_ADDR} -C ${CHANNEL} -n ${CHAINCODE} --tls --cafile ${ORDERER_CERT} -c '{"Args":["invoke","a","b","10"]}'
peer chaincode query -C ${CHANNEL} -n ${CHAINCODE} -c '{"Args":["query","b"]}'
```

  * CivisBlockchain

```
# Leave CLI environment
exit
# Backup channel configuration block
docker cp cli-ChainOpsBC1Orderer:/opt/gopath/src/github.com/hyperledger/fabric/peer/${CHANNEL}.block .
# Kill CLI container
docker stop cli-ChainOpsBC1Orderer
docker rm cli-ChainOpsBC1Orderer
# override for CivisBlockchain / civis-blockchain.org
cli_MSP=CivisBlockchainBC1PeerMSP
cli_ORGA=pr-bc1.civis-blockchain.org
idx=0
# Rebuild CLI container
echo cli_MSP=${cli_MSP} >> .env
echo cli_ORGA=${cli_ORGA} >> .env
docker-compose -f docker-compose.yaml up -d cli.or-bc1.chain-ops.net
# Restore channel configuration block
docker cp ${CHANNEL}.block cli-ChainOpsBC1Orderer:/opt/gopath/src/github.com/hyperledger/fabric/peer/
# Re-enter CLI container
echo CORE_PEER_ADDRESS="peer${idx}.${cli_ORGA}:7051" >> ../util/env
echo CORE_PEER_LOCALMSPID=${cli_MSP} >> ../util/env
# Re-enter CLI environment
docker exec -it cli-ChainOpsBC1Orderer /bin/bash
source /opt/blockchain-coop/env
# Join channel
peer channel join -b ${CHANNEL}.block
# Install chaincode
peer chaincode install -n ${CHAINCODE} -v 1.0 -p blockchain-coop/go/chaincode_example02/
# Run a query to spawn the container on the target peer
peer chaincode query -C ${CHANNEL} -n ${CHAINCODE} -c '{"Args":["query","a"]}'
# Verify this peer can do a transaction
peer chaincode invoke -o ${ORDERER_ADDR} -C ${CHANNEL} -n ${CHAINCODE} --tls --cafile ${ORDERER_CERT} -c '{"Args":["invoke","a","b","10"]}'
peer chaincode query -C ${CHANNEL} -n ${CHAINCODE} -c '{"Args":["query","b"]}'
```

  * Thingagora

```
# Leave CLI environment
exit
# Backup channel configuration block
docker cp cli-ChainOpsBC1Orderer:/opt/gopath/src/github.com/hyperledger/fabric/peer/${CHANNEL}.block .
# Kill CLI container
docker stop cli-ChainOpsBC1Orderer
docker rm cli-ChainOpsBC1Orderer
# override for Thingagora / thingagora.org
cli_MSP=ThingagoraBC1PeerMSP
cli_ORGA=pr-bc1.thingagora.org
idx=0
# Rebuild CLI container
echo cli_MSP=${cli_MSP} >> .env
echo cli_ORGA=${cli_ORGA} >> .env
docker-compose -f docker-compose.yaml up -d cli.or-bc1.chain-ops.net
echo CORE_PEER_ADDRESS="peer${idx}.${cli_ORGA}:7051" >> ../util/env
echo CORE_PEER_LOCALMSPID=${cli_MSP} >> ../util/env
# Restore channel configuration block
docker cp ${CHANNEL}.block cli-ChainOpsBC1Orderer:/opt/gopath/src/github.com/hyperledger/fabric/peer/
# Re-enter CLI container
echo CORE_PEER_ADDRESS="peer${idx}.${cli_ORGA}:7051" >> ../util/env
echo CORE_PEER_LOCALMSPID=${cli_MSP} >> ../util/env
# Re-enter CLI environment
docker exec -it cli-ChainOpsBC1Orderer /bin/bash
source /opt/blockchain-coop/env
# Join channel
peer channel join -b ${CHANNEL}.block
# Install chaincode
peer chaincode install -n ${CHAINCODE} -v 1.0 -p blockchain-coop/go/chaincode_example02/
# Run a query to spawn the container on the target peer
peer chaincode query -C ${CHANNEL} -n ${CHAINCODE} -c '{"Args":["query","a"]}'
# Verify this peer can do a transaction
peer chaincode invoke -o ${ORDERER_ADDR} -C ${CHANNEL} -n ${CHAINCODE} --tls --cafile ${ORDERER_CERT} -c '{"Args":["invoke","a","b","10"]}'
peer chaincode query -C ${CHANNEL} -n ${CHAINCODE} -c '{"Args":["query","b"]}'
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


