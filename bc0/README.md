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
export CHANNEL=sandbox
```

  * Channel configuration transaction 

```
configtxgen -profile BlockchainCoopBC0PeerChannels -outputCreateChannelTx ./config/${CHANNEL}.tx -channelID $CHANNEL
```

  * Anchor peer transaction

```
configtxgen -profile BlockchainCoopBC0PeerChannels -outputAnchorPeersUpdate ./config/BlockchainCoopBC0PeerMSPanchors.tx -channelID ${CHANNEL} -asOrg ThingagoraBC0Peer
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

echo cli_ORGA=${ORGA} >> .env
echo cli_USER=Admin >> .env
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

### Start CLI container locally

```
docker-compose -f docker-compose.yaml up -d cli.pr-bc0.thingagora.org
```

## Network setup on a local machine

### Start network locally with only one peer 

```
docker-compose -f docker-compose.yaml up -d ca.pr-bc0.thingagora.org orderer0.or-bc0.thingagora.org peer0.pr-bc0.thingagora.org cli.pr-bc0.thingagora.org
```

### Use the CLI container environment


  * Runtime CLI configuration

```
cli_ORGA=pr-bc0.thingagora.org
echo cli_ORGA=${cli_ORGA} > ../util/env

# orderer address and certificate
echo ORDERER_ADDR="orderer0.or-bc0.thingagora.org:7050" >> ../util/env
echo ORDERER_CERT="/etc/hyperledger/orderer/tlsca.or-bc0.thingagora.org-cert.pem" >> ../util/env

# current session chaincode
echo CHANNEL="sandbox" >> ../util/env
echo CHAINCODE="ex02" >> ../util/env

# override target peer if needed
echo CORE_PEER_ADDRESS="peer0.${cli_ORGA}:7051" >> ../util/env

# Enter CLI environment
docker exec -it cli-ThingagoraBC0Peer /bin/bash
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
peer chaincode instantiate -o ${ORDERER_ADDR} --tls --cafile ${ORDERER_CERT} -C ${CHANNEL} -n ${CHAINCODE} -v 1.0 -c '{"Args":["init","a", "100", "b","200"]}' -P "OR ('ThingagoraBC0PeerMSP.member')"
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
npm install ../sdk/node/
export PATH=$(pwd)/node_modules/blockchain-coop:$PATH
source .env
```

  * Enroll the bc0 admin

```
bcc-cli.js enroll $ca__ADMIN $ca__PASSWD bc0
bcc-cli.js check $ca__ADMIN
bcc-cli.js query $ca__ADMIN peer0 bc0 sandbox ex02 query a
bcc-cli.js invoke $ca__ADMIN peer0:bc0 sandbox ex02 invoke a b 1
bcc-cli.js query $ca__ADMIN peer0 bc0 sandbox ex02 query a
```

  * Register and enroll a new user

```
bcc-cli.js register $ca__ADMIN $ca__PASSWD bc0 Fred ABCD1234
bcc-cli.js enroll Fred ABCD1234 bc0
bcc-cli.js check Fred
bcc-cli.js query Fred peer0 bc0 sandbox ex02 query b
bcc-cli.js invoke Fred peer0:bc0 sandbox ex02 invoke a b 50
bcc-cli.js query Fred peer0 bc0 sandbox ex02 query b
```


### Stop network and cleanup

```
docker-compose -f docker-compose.yaml down
```

```
git clean -fdx
```

### Upgrade network

  * Update crypto for peer1.pr-bc0.thingagora.org

```
cryptogen extend --input=crypto-config --config=crypto-config.yaml
```

  * Package peer1.pr-bc0.thingagora.org

```
peer-archive.sh peer1 pr-bc0.thingagora.org
scp peer_peer1_pr-bc0.thingagora.org.tgz blockchain@peer1.pr-bc0.thingagora.org:
```

  * Start container on remote host

```
ssh blockchain@peer1.pr-bc0.thingagora.org
tar xvzf peer_peer1_pr-bc0.thingagora.org.tgz
docker-compose -f docker-compose.yaml up -d peer1.pr-bc0.thingagora.org
```

  * Runtime CLI configuration

```
# override target peer
echo CORE_PEER_ADDRESS="peer1.${cli_ORGA}:7051" >> ../util/env
```

  * Join existing channels

```
# Enter CLI environment
docker exec -it cli-ThingagoraBC0Peer /bin/bash
source /opt/blockchain-coop/env
# Retrieve channel configuration from another peer
CORE_PEER_ADDRESS="peer0.${cli_ORGA}:7051"
peer channel fetch config ${CHANNEL}.block --channelID ${CHANNEL} -o ${ORDERER_ADDR} --tls --cafile ${ORDERER_CERT}
# New peer now joins the channel
CORE_PEER_ADDRESS="peer1.${cli_ORGA}:7051"
peer channel join -b ${CHANNEL}.block
```

  * Install and run chaincode

```
peer chaincode install -n ${CHAINCODE} -v 1.0 -p blockchain-coop/go/chaincode_example02/
# Chaincode is already instantiated, a query will spawn it on the new peer
peer chaincode query -C ${CHANNEL} -n ${CHAINCODE} -c '{"Args":["query","a"]}'
```
