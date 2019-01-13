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
export ORDERER=orderer.bclan:7050
export CA_FILE=/etc/hyperledger/orderer/tlsca.bclan-cert.pem
export CHANNEL_NAME=sandbox
```

```
peer channel create -o ${ORDERER} -c ${CHANNEL_NAME} -f /etc/hyperledger/config/channel.tx --tls --cafile ${CA_FILE}
peer channel join -b ${CHANNEL_NAME}.block
```

```
peer chaincode install -n ex02 -v 1.0 -p blockchain-coop/chaincode_example02/go/
peer chaincode instantiate -o ${ORDERER} --tls --cafile ${CA_FILE} -C ${CHANNEL_NAME} -n ex02 -v 1.0 -c '{"Args":["init","a", "100", "b","200"]}' -P "OR ('BlockchainLANCoopMSP.member')"
```

```
peer chaincode query -C ${CHANNEL_NAME} -n ex02 -c '{"Args":["query","a"]}'
peer chaincode invoke -o ${ORDERER} -C ${CHANNEL_NAME} -n ex02 --tls --cafile ${CA_FILE} -c '{"Args":["invoke","a","b","10"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ex02 -c '{"Args":["query","b"]}'
```

```
export CHAINCODE=ssm
export VERSION=0.1.0
```

```
peer chaincode install -n ${CHAINCODE} -v ${VERSION} -p blockchain-coop/ssm/
```

```
peer chaincode instantiate -o ${ORDERER} --tls --cafile ${CA_FILE} -C ${CHANNEL_NAME} -n ${CHAINCODE} -v ${VERSION} -c '{"Args":["init","[{\"name\": \"Adam\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3MO+2nIRi/cs4WbE+ykzA1ErTfs0QmBIdpZoAsU7YVMKBnBNulxhy2BI93QHK9uQreLhANBDexagMZg9ZzCxtKLi9UNHSm08099znPfMKn2cITHI8ShyZC7OogsbNmqrY0iy01r4IVpPi4CMNhLTCWyLGWS+L0hsmZOQQWV5BeER4nufBgGmA8plD14T/AXaHF7pMJAGlvauqjcjhb9YAoDUjSmdy4h3KzNq0c1KSQwORgQhgGItUxs5X8jvAXsikRDs7OkqbEDWpSf5z6FSyenvPmnplrqL/5bjiis6ObbOA+BjpMpyuouXOA3WuGv61a5Wrx62bcfeCx9471EKFQIDAQAB\"}]"]}' -P "OR ('BlockchainLANCoopMSP.member')"
```

```
peer chaincode upgrade -o ${ORDERER} --tls --cafile ${CA_FILE} -C ${CHANNEL_NAME} -n ${CHAINCODE} -v ${VERSION} -c '{"Args":["init","[{\"name\": \"Adam\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3MO+2nIRi/cs4WbE+ykzA1ErTfs0QmBIdpZoAsU7YVMKBnBNulxhy2BI93QHK9uQreLhANBDexagMZg9ZzCxtKLi9UNHSm08099znPfMKn2cITHI8ShyZC7OogsbNmqrY0iy01r4IVpPi4CMNhLTCWyLGWS+L0hsmZOQQWV5BeER4nufBgGmA8plD14T/AXaHF7pMJAGlvauqjcjhb9YAoDUjSmdy4h3KzNq0c1KSQwORgQhgGItUxs5X8jvAXsikRDs7OkqbEDWpSf5z6FSyenvPmnplrqL/5bjiis6ObbOA+BjpMpyuouXOA3WuGv61a5Wrx62bcfeCx9471EKFQIDAQAB\"}]"]}' -P "OR ('BlockchainLANCoopMSP.member')"
```

```
peer chaincode invoke -o ${ORDERER} -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile ${CA_FILE} -c '{"Args":["register", "{\"name\":\"Bob\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4v2ZYVhQaG7HdhCsrFKlxG1kWLrApCgb2tUjdXLjc4WVG12ZC2eW1b5WZZnsGmS46eCDZyxZsJTxTbKjlKb8FcWWfs0tEixL08gqBKUH9+FD8GaEdtiaOdQJ+yehUBZ5VEty9OrLUMW5L3Ftr9kGmbbtAMRkyRXTT6KEXemm3xRpX/Z2FQzmFmcucbArjyu/LvzlDHJwCiQEQS3R9UCGnjYLZEBYqj7tEEP5cnsm5egwe/EWXx9rvZH9HK3AYVgraE69qZ+FhWkErIqHWmctgPbBYBPXKCfBMzf4INMA4+c5gaCygz4RRFcr10OjGCAS/IiKaLl0X7ehbt6yrYyxEwIDAQAB\"}", "Adam", "GotPEetQ34tbVodiGw3prtYWYjf06mMJX5j4X6n8m+KbMYQt0GMRtmVo88jRbXqD71IRlgPfe6dnNZ0dZRIZSUGAk57Y6fuADC0vwDciEg2+IXEc/F4vrMAEh3KXPMwb3MskRA+K1IO91szwcrrn3Y74odrBzmSypTjO60GmPAJybJ9r/5ynafDZFM5wmzgNNecJ51Yz8nltHPhhMOzDIE7pETvrlK58XnarySl81WMgR9GIO64A+WixsEsyURCiYQwWVf9XKAM6a9bDvm8h5fUvfrfktmruz8CB/qJgBLD60WD3qvSCPoN8BgCzI+QgAebyqNejNPbWSP7Z18JpxQ=="]}'
peer chaincode invoke -o ${ORDERER} -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile ${CA_FILE} -c '{"Args":["register", "{\"name\":\"Sam\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp6IgdpSmqt6UdZU1ZL4UY3Biog3iOkYiDAkDJTKNzaWVM8vYgrBn8out3xJ7QoFmAVjyvgLiFq4+y5vG4aRRfRuTRuzDJN1a4LT21QbGGmBppx/F+rSU1wLv+zg1U/uNu1XvxojrgAIzF3+2GmkEn8w1LqREhaY+h5VPgaixzIIcDotTp8FbdcFFAXwpEUBbwTvpqq91AdzpoYRjYQMWBkgEuNms6eXnzalSw0L3rNet7GbDPS5USUXFc4qm1VWQ3yGU6ur+4T0tsn9WPbEraG6ddIs0y1MOonxl0h97Stk14XXVkwI1vRWTK1C8HMHdu9WUB9uNj95IfL6EVNV8fQIDAQAB\"}", "Adam", "SJ/naOMctg3X6MbbatkU4SOwTG+P8BbW6Ggxm0ItMd6+KAlQSMVixTCiOBpoU7FuiENxPZtiuk2jz5cE+mngFSMqurgAUpN03bfBxmv6TD8OhyAakXENXQRhquBLz2GW4AUJGCkrL8IcrSft27Takxls9ObkinOdjaWdzY6D7q1mTiuXVdr9ekXB9TjEtLeLaY+fbSPX8AO0TkADUl3FC60sX8UBaCU/7CbBjsCvKSZ/eAsh5JK2D+/MLoNJL1RRkn2+ChkVeKI9Hw+niFQBMJO/+F8hLgnyO4xBFkUTjDpg2IlLAkSPdHzXj0sgSHmugCGSIEOW/3lFFtqD3VPFRQ=="]}'
peer chaincode invoke -o ${ORDERER} -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile ${CA_FILE} -c '{"Args":["create", "{\"name\": \"Car dealership\", \"transitions\": [{\"from\": 0, \"to\": 1, \"role\": \"Seller\", \"action\": \"Sell\"}, {\"from\": 1, \"to\": 2, \"role\": \"Buyer\", \"action\": \"Buy\"}]}", "Adam", "rrPpaH88z7CcTSGXsizSGAquwT2jBVW253ICAzQfF/wy28i/XOHC1FwQAWejYOLdHhHijzbZyPSbruB9dLx/n4UEXGc74BV35nYTiFHpdUNUouPW9yu9CoAGBlDV9XMeUQyZHd3YdOMN/+15r7FM8wPCry6H//eL5D/R5a3AXywrKGGFwU5XQGc9BjX1kgza/X2NvNp9XfxXTi3cnlcgKImM2fjGphRYbUSmEXNvszSce/lIMfdvCaR2ZDOhWzLDzkhLaD9BpBTyGsC5JoH56kVUuG5skKS8W6YYKL5uMHvaM5JjDs159uHIITVomUXRfbgTSjqYyjsaw0fh0B8pKQ=="]}'
peer chaincode invoke -o ${ORDERER} -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile ${CA_FILE} -c '{"Args":["start","{\"ssm\": \"Car dealership\", \"session\": \"deal20181201\", \"public\": \"Used car for 100 dollars.\", \"roles\": {\"Bob\": \"Buyer\", \"Sam\": \"Seller\"}}", "Adam", "Kkn/45dLEkqJiLVllI/Ti7QjuWiRmGNwole/0+jWT4iWsnDOwm5OdvOprvw1V+WhjzpQ9AuI8hUd/cs/jcFcApI34aHlANv4XhZoGYD81qbdIYD8xjBvS3ql9ft931dWJp4sTrz7ofjHzaLanrQtQccVqhFBaRZXAitF21iLjQA8qlOmebWya8Z/GRGfYETGWWmlAGjuD3DFrdIbc/hIHBzUhiG5idiWIhmt8i/QybeeAIsMldYF2DBnnBFJ4jeGGGdt4gLYK1B0l3sZY4dbzIeZHHfDu3mBtUo9oWD8Lon6oVNEMswU6mVnugi5/wm4K9kjXAsFQXgi9yD1t1pcFQ=="]}' 

peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["admin", "Adam"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["user", "Bob"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["user", "Sam"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["ssm", "Car dealership"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["session", "deal20181201"]}'

peer chaincode invoke -o ${ORDERER} -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile ${CA_FILE} -c '{"Args":["perform", "Sell", "{\"session\": \"deal20181201\", \"iteration\": 0}", "Sam", "boDJyvO5RWnC6AMq01AebejNb/hpLo69PLGqob2S1bePNutNVw0EZSFvzbo7NX4SyEZNM+Oa3mapvLQe+i6J1G1qZMPajcD0fuO0EjTIUJZ15BqnCdEtkJIVHbA0uTbEDnpLFmINBff4cQ1OdyJ3wulGR/gOea2Uo7198B8AvLl00WZ3Qv1CXG7kih5FFyc+pALOd/h1qh1x3OicMGn5Pz/fvXc5F2o/PV30f59aqg2TQE0VpJgpwXJ/4ZnB0MUmjnoy1w8jaDUAao+ctNv99UEIxVJbhEK6OnnPrYjFWBr+yQGMHd4X9aJvxIlLVhxyz0dAuFZJ0DKUh198julAag=="]}'
peer chaincode invoke -o ${ORDERER} -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile ${CA_FILE} -c '{"Args":["perform", "Buy", "{\"session\": \"deal20181201\", \"iteration\": 1}", "Bob", "W+xeR+NQKaWz3gAnpScK0wMtpyAkTcihcw6C0akA0q2PaP1mpmC37Xl94neBX+xiRxsIXvLswCZyMrFX1GBGhR/xIO96IOxG0i4lF67wfy4jTnXGqHm+Ul8PfALfcUuPog1rXX/MRPscyhy5AJ9o2xLNbctXDmG6P/1RzD5q3wKDeNF+gOtEjg5baKko1Did+/BtwKl66TXyMTi5rKej+nUIOt0WsLlB95qRrc37zcju9Dz9H4l+TsjTqeOLVERwf2GU2EFsS+X5yOCS2pcmkWTkv88e1jDksxXxihsd7ecCy+FnyVbQXrH5UhmpAtQyxOsWehSQ55B8JuLIQ1LPRQ=="]}'
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



