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
configtxgen -profile YriarteMaisonChannels -outputCreateChannelTx ./config/channel.tx -channelID $CHANNEL_NAME
```

  * Anchor peer transaction

```
configtxgen -profile YriarteMaisonChannels -outputAnchorPeersUpdate ./config/YriarteMaisonMSPanchors.tx -channelID $CHANNEL_NAME -asOrg YriarteMaison
```
