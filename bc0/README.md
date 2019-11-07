# Network configuration: bc0


## Organization cryptography generation


### Environment variables

```
BLOCKCHAIN=bc0
BLOCKCHAIN_NAME=BC0
  
ORDERER_ORGNAME=ThingagoraBC0Orderer
ORDERER_MSP=ThingagoraBC0OrdererMSP
ORDERER_ORGA=or-bc0.thingagora.org
  
ORGNAME=ThingagoraBC0Peer
MSP=ThingagoraBC0PeerMSP
ORGA=pr-bc0.thingagora.org

CHANNEL=sandbox
```

### Execution

#### Crypto

```
export PATH=$PWD/../util:$PATH

cryptogen generate --config=./crypto-config.yaml

export FABRIC_CFG_PATH=$PWD

configtxgen -profile ${BLOCKCHAIN_NAME}OrdererGenesis  -channelID ${BLOCKCHAIN} -outputBlock ./config/${BLOCKCHAIN}_genesis.block
configtxgen -profile ${BLOCKCHAIN_NAME}PeerChannels -outputCreateChannelTx ./config/${CHANNEL}.tx -channelID $CHANNEL
configtxgen -profile ${BLOCKCHAIN_NAME}PeerChannels -outputAnchorPeersUpdate ./config/${ORGA}_${CHANNEL}.tx -channelID ${CHANNEL} -asOrg ${ORGNAME}

```

#### Archive

```
echo COMPOSE_PROJECT_NAME="${BLOCKCHAIN}" > .env
echo ca__CA_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/${ORGA}/ca/*_sk)) >> .env
echo ca__TLS_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/${ORGA}/tlsca/*_sk)) >> .env
echo ca__ADMIN=$(cat /dev/urandom | xxd | head -n 1 | cut -b 10-49 | sed "s/ //g") >> .env
echo ca__PASSWD=$(cat /dev/urandom | xxd | head -n 1 | cut -b 10-49 | sed "s/ //g") >> .env

echo cli_MSP=${MSP} >> .env
echo cli_ORGA=${ORGA} >> .env
echo cli_USER=Admin >> .env

cp .env env_${MSP}

idx=0
echo COMPOSE_PROJECT_NAME="${BLOCKCHAIN}" > .env
orderer-archive.sh orderer${idx} ${ORDERER_ORGA}

echo ORGA=${ORGA} > env_ca_${ORGA}
cp env_${MSP} .env
ca-archive.sh ${ORGA}

idx=0
echo ORGA=${ORGA} > env_peer${idx}_${ORGA}
echo idx=${idx} >> env_peer${idx}_${ORGA}
cp env_${MSP} .env
peer-archive.sh peer${idx} ${ORGA}

idx=1
echo ORGA=${ORGA} > env_peer${idx}_${ORGA}
echo idx=${idx} >> env_peer${idx}_${ORGA}
cp env_${MSP} .env
peer-archive.sh peer${idx} ${ORGA}

user-archive.sh Admin ${ORGA} ${ORDERER_ORGA}
user-archive.sh User1 ${ORGA}
mv user_User1_${ORGA}.tgz reader_User1_${ORGA}.tgz
user-archive.sh User1 ${ORGA} ${ORDERER_ORGA}

```

#### Deploy

```
idx=0

echo "idx=${idx}" > env
echo "ORDERER_ORGA=${ORDERER_ORGA}" >> env
scp env blockchain@orderer${idx}.${ORDERER_ORGA}:
scp orderer_orderer${idx}_${ORDERER_ORGA}.tgz blockchain@orderer${idx}.${ORDERER_ORGA}:

ssh blockchain@orderer${idx}.${ORDERER_ORGA}
source ./env
tar xvzf orderer_orderer${idx}_${ORDERER_ORGA}.tgz
cat env >> .env
docker-compose -f docker-compose.yaml up -d orderer${idx}.${ORDERER_ORGA}
rm env .env docker-compose.yaml orderer_orderer${idx}_${ORDERER_ORGA}.tgz
exit


scp env_ca_${ORGA} blockchain@ca.${ORGA}:env
scp ca_${ORGA}.tgz blockchain@ca.${ORGA}:

ssh blockchain@ca.${ORGA}
source ./env
tar xvzf ca_${ORGA}.tgz
cat env >> .env
docker-compose -f docker-compose.yaml up -d ca.${ORGA}
rm env .env docker-compose.yaml ca_${ORGA}.tgz
exit


idx=0

scp env_peer${idx}_${ORGA} blockchain@peer${idx}.${ORGA}:env
scp peer_peer${idx}_${ORGA}.tgz blockchain@peer${idx}.${ORGA}:

ssh blockchain@peer${idx}.${ORGA}
source ./env
tar xvzf peer_peer${idx}_${ORGA}.tgz
cat env >> .env
docker-compose -f docker-compose.yaml up -d peer${idx}.${ORGA}
rm env .env docker-compose.yaml peer_peer${idx}_${ORGA}.tgz
exit

idx=1

scp env_peer${idx}_${ORGA} blockchain@peer${idx}.${ORGA}:env
scp peer_peer${idx}_${ORGA}.tgz blockchain@peer${idx}.${ORGA}:

ssh blockchain@peer${idx}.${ORGA}
source ./env
tar xvzf peer_peer${idx}_${ORGA}.tgz
cat env >> .env
docker-compose -f docker-compose.yaml up -d peer${idx}.${ORGA}
rm env .env docker-compose.yaml peer_peer${idx}_${ORGA}.tgz
exit

```

#### Command Line Interface

Create container

```
cli_MSP=${MSP}
cli_ORGA=${ORGA}
cli_USER=Admin
tar xvzf user_${cli_USER}_${cli_ORGA}.tgz

echo cli_MSP=${cli_MSP} > .env
echo cli_ORGA=${cli_ORGA} >> .env
echo cli_USER=${cli_USER} >> .env
echo BLOCKCHAIN=${BLOCKCHAIN} >> .env
echo ORDERER_ORGA=${ORDERER_ORGA} >> .env
docker-compose -f docker-compose.yaml up -d cli.${ORDERER_ORGA}

idx=0

cp .env util/env
# orderer address and certificate
echo ORDERER_ADDR="orderer${idx}.${ORDERER_ORGA}:7050" >> util/env
echo ORDERER_CERT="/etc/hyperledger/orderer/tlsca.${ORDERER_ORGA}-cert.pem" >> util/env
```

Enter and leave container

```
# Enter container
docker exec -it cli-${ORDERER_ORGNAME} /bin/bash
# Use environment variables in the container
source /opt/bc0/env
# Exit container
exit
```

#### Channel

Enter container and source environment

```
# CHANNEL=sandbox
# Channel creation in CLI environment
peer channel create -o ${ORDERER_ADDR} -c ${CHANNEL} -f /etc/hyperledger/config/${CHANNEL}.tx --tls --cafile ${ORDERER_CERT}
# Channel backup 
cp ${CHANNEL}.block /opt/bc0
# Joining channel
peer channel join -b ${CHANNEL}.block
# CORE_PEER_ADDRESS=
# Join...
```

#### Chaincode

Copy SSM sources

```
cp -r ../../blockchain-ssm/chaincode/go chaincode
```

Enter container and source environment

```
# package chaincode
CHAINCODE=ssm
VERSION=0.4.2
peer chaincode package -n ${CHAINCODE} -p bc0/go/${CHAINCODE} -v ${VERSION} ${CHAINCODE}-${VERSION}.pak
# Chaincode package backup 
cp ${CHAINCODE}-${VERSION}.pak /opt/bc0
# Chaincode install
peer chaincode install -n ${CHAINCODE} -v ${VERSION} -p ${CHAINCODE}-${VERSION}.pak
# CORE_PEER_ADDRESS=
# install...
```

```
# instantiate with admins
export PATH=/opt/bc0:$PATH
echo -n '{"Args":["init","[' > init.arg
json_agent lyr-main lyr-main.pub  | jq . -cM | sed 's/"/\\"/g' | tr -d "\n" >> init.arg
echo -n ',' >> init.arg
json_agent lyr-share lyr-share.pub  | jq . -cM | sed 's/"/\\"/g' | tr -d "\n" >> init.arg
peer chaincode instantiate -o ${ORDERER_ADDR} --tls --cafile ${ORDERER_CERT} -C ${CHANNEL} -n ${CHAINCODE} -v ${VERSION} -c $(cat init.arg) -P "OR ('ThingagoraBC0PeerMSP.member')"
peer chaincode query -C ${CHANNEL} -n ${CHAINCODE} -c '{"Args":["list", "admin"]}'
```
