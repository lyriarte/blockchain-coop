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
