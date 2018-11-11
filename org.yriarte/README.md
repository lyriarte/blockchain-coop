# Network configuration: yriarte.org


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
configtxgen -profile YriarteOrdererGenesis -outputBlock ./config/genesis.block
```

#### Channels

```
export CHANNEL_NAME=sandbox
```

  * Channel configuration transaction 

```
configtxgen -profile YriarteMauguioChannels -outputCreateChannelTx ./config/channel.tx -channelID $CHANNEL_NAME
```

  * Anchor peer transaction

```
configtxgen -profile YriarteMauguioChannels -outputAnchorPeersUpdate ./config/YriarteMauguioMSPanchors.tx -channelID $CHANNEL_NAME -asOrg YriarteMauguio
```

## Network setup

### Docker containers environment

```
echo -n > .env
echo ca__CA_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/mauguio.yriarte.org/ca/*_sk)) >> .env
echo ca__TLS_KEYFILE=$(basename $(ls crypto-config/peerOrganizations/mauguio.yriarte.org/tlsca/*_sk)) >> .env
echo ca__ADMIN=CHANGE_ME >> .env
echo ca__PASSWD=CHANGE_ME >> .env
echo telerondb__USERNAME=CHANGE_ME >> .env
echo telerondb__PASSWORD=CHANGE_ME >> .env
```

### Start network

```
docker-compose -f docker-compose.yaml up -d
```

### Use the CLI container environment

```
docker exec -it cli-teleron /bin/bash
```

```
export CHANNEL_NAME=sandbox
peer channel create -o orderer.yriarte.org:7050 -c ${CHANNEL_NAME} -f /etc/hyperledger/config/channel.tx
peer channel join -b ${CHANNEL_NAME}.block
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



