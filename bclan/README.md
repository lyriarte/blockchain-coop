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
export CHAINCODE=ssm
export VERSION=144ab10
peer chaincode install -n ${CHAINCODE} -v ${VERSION} -p blockchain-coop/ssm/
```

```
export CHANNEL_NAME=sandbox
export CHAINCODE=ssm
export VERSION=144ab10
peer chaincode instantiate -o orderer.bclan:7050 --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -C ${CHANNEL_NAME} -n ${CHAINCODE} -v ${VERSION} -c '{"Args":["init","[{\"name\": \"lyr-xps13\",\"pub\":\"AAAAB3NzaC1yc2EAAAADAQABAAABAQDDXm+Oy0Kq/GTwJnBBNp9BOA8neEoRSb1YV6V6oPbPSoHOmLQP00t6IuFy04lPY/KibkBv2G3x7OTZfT6NgXQ79Xg9gSRqPBB/ZQP4pPi1ifGBsM2v3Qe8DPQ6yrwcVdzr9a9iVXRhQvMPLBZydYKYA1ZpV6dTr3oXunuBXov/HmwxR5M8TQ21znHOUbO6AiWiXgsMC0E5pWjoLWhIzVMeOCmags4FvLmTIaCwRPmW8bOt7IVXWdVO3l4mS5v/M5zOSpf2s0gpdkkOtEy/nUFn6hBT7tIeVV9XI62wNB4YjivkF/Giw+czjZwa6V+d/PzLHtsuzuBEmdPeO9FfYnkb\"}]"]}' -P "OR ('BlockchainLANCoopMSP.member')"
```

```
export CHANNEL_NAME=sandbox
export CHAINCODE=ssm
export VERSION=144ab10

peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["register", "{\"name\": \"John Doe\", \"pub\": \"XXXAAA\"}", "lyr-xps13", "XYZ"]}'
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["register", "{\"name\": \"Joe Black\", \"pub\": \"YYYBBB\"}", "lyr-xps13", "XYZ"]}'
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["create", "{\"name\": \"Car dealership\", \"transitions\": [{\"from\": 0, \"to\": 1, \"role\": \"Seller\", \"action\": \"Sell\"}, {\"from\": 1, \"to\": 2, \"role\": \"Buyer\", \"action\": \"Buy\"}]}", "lyr-xps13", "XYZ"]}'
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["start","{\"ssm\": \"Car dealership\", \"session\": \"deal20181201\", \"current\": 0, \"public\": \"Car dealer 2018 public\", \"private\": {\"John Doe\": \"XXX\",\"Joe Black\": \"YYY\"}, \"roles\": {\"Buyer\": \"John Doe\", \"Seller\": \"Joe Black\"}}", "lyr-xps13", "XYZ"]}' 

peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["admin", "lyr-xps13"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["user", "John Doe"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["user", "Joe Black"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["ssm", "Car dealership"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["session", "deal20181201"]}'

peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["perform", "Sell", "deal20181201", "Joe Black", "ABC"]}'
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["perform", "Buy", "deal20181201", "John Doe", "EFG"]}'
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



