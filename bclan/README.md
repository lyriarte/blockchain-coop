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
export VERSION=22dd1e4
peer chaincode install -n ${CHAINCODE} -v ${VERSION} -p blockchain-coop/ssm/
```

```
export CHANNEL_NAME=sandbox
export CHAINCODE=ssm
export VERSION=22dd1e4
peer chaincode instantiate -o orderer.bclan:7050 --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -C ${CHANNEL_NAME} -n ${CHAINCODE} -v ${VERSION} -c '{"Args":["init","[{\"name\": \"Adam\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3MO+2nIRi/cs4WbE+ykzA1ErTfs0QmBIdpZoAsU7YVMKBnBNulxhy2BI93QHK9uQreLhANBDexagMZg9ZzCxtKLi9UNHSm08099znPfMKn2cITHI8ShyZC7OogsbNmqrY0iy01r4IVpPi4CMNhLTCWyLGWS+L0hsmZOQQWV5BeER4nufBgGmA8plD14T/AXaHF7pMJAGlvauqjcjhb9YAoDUjSmdy4h3KzNq0c1KSQwORgQhgGItUxs5X8jvAXsikRDs7OkqbEDWpSf5z6FSyenvPmnplrqL/5bjiis6ObbOA+BjpMpyuouXOA3WuGv61a5Wrx62bcfeCx9471EKFQIDAQAB\"}]"]}' -P "OR ('BlockchainLANCoopMSP.member')"
```

```
peer chaincode upgrade -o orderer.bclan:7050 --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -C ${CHANNEL_NAME} -n ${CHAINCODE} -v ${VERSION} -c '{"Args":["init","[{\"name\": \"Adam\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3MO+2nIRi/cs4WbE+ykzA1ErTfs0QmBIdpZoAsU7YVMKBnBNulxhy2BI93QHK9uQreLhANBDexagMZg9ZzCxtKLi9UNHSm08099znPfMKn2cITHI8ShyZC7OogsbNmqrY0iy01r4IVpPi4CMNhLTCWyLGWS+L0hsmZOQQWV5BeER4nufBgGmA8plD14T/AXaHF7pMJAGlvauqjcjhb9YAoDUjSmdy4h3KzNq0c1KSQwORgQhgGItUxs5X8jvAXsikRDs7OkqbEDWpSf5z6FSyenvPmnplrqL/5bjiis6ObbOA+BjpMpyuouXOA3WuGv61a5Wrx62bcfeCx9471EKFQIDAQAB\"}]"]}' -P "OR ('BlockchainLANCoopMSP.member')"
```

```
export CHANNEL_NAME=sandbox
export CHAINCODE=ssm
export VERSION=22dd1e4
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["register", "{\"name\":\"Bob\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4v2ZYVhQaG7HdhCsrFKlxG1kWLrApCgb2tUjdXLjc4WVG12ZC2eW1b5WZZnsGmS46eCDZyxZsJTxTbKjlKb8FcWWfs0tEixL08gqBKUH9+FD8GaEdtiaOdQJ+yehUBZ5VEty9OrLUMW5L3Ftr9kGmbbtAMRkyRXTT6KEXemm3xRpX/Z2FQzmFmcucbArjyu/LvzlDHJwCiQEQS3R9UCGnjYLZEBYqj7tEEP5cnsm5egwe/EWXx9rvZH9HK3AYVgraE69qZ+FhWkErIqHWmctgPbBYBPXKCfBMzf4INMA4+c5gaCygz4RRFcr10OjGCAS/IiKaLl0X7ehbt6yrYyxEwIDAQAB\"}", "Adam", "GotPEetQ34tbVodiGw3prtYWYjf06mMJX5j4X6n8m+KbMYQt0GMRtmVo88jRbXqD71IRlgPfe6dnNZ0dZRIZSUGAk57Y6fuADC0vwDciEg2+IXEc/F4vrMAEh3KXPMwb3MskRA+K1IO91szwcrrn3Y74odrBzmSypTjO60GmPAJybJ9r/5ynafDZFM5wmzgNNecJ51Yz8nltHPhhMOzDIE7pETvrlK58XnarySl81WMgR9GIO64A+WixsEsyURCiYQwWVf9XKAM6a9bDvm8h5fUvfrfktmruz8CB/qJgBLD60WD3qvSCPoN8BgCzI+QgAebyqNejNPbWSP7Z18JpxQ=="]}'
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["register", "{\"name\":\"Sam\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp6IgdpSmqt6UdZU1ZL4UY3Biog3iOkYiDAkDJTKNzaWVM8vYgrBn8out3xJ7QoFmAVjyvgLiFq4+y5vG4aRRfRuTRuzDJN1a4LT21QbGGmBppx/F+rSU1wLv+zg1U/uNu1XvxojrgAIzF3+2GmkEn8w1LqREhaY+h5VPgaixzIIcDotTp8FbdcFFAXwpEUBbwTvpqq91AdzpoYRjYQMWBkgEuNms6eXnzalSw0L3rNet7GbDPS5USUXFc4qm1VWQ3yGU6ur+4T0tsn9WPbEraG6ddIs0y1MOonxl0h97Stk14XXVkwI1vRWTK1C8HMHdu9WUB9uNj95IfL6EVNV8fQIDAQAB\"}", "Adam", "SJ/naOMctg3X6MbbatkU4SOwTG+P8BbW6Ggxm0ItMd6+KAlQSMVixTCiOBpoU7FuiENxPZtiuk2jz5cE+mngFSMqurgAUpN03bfBxmv6TD8OhyAakXENXQRhquBLz2GW4AUJGCkrL8IcrSft27Takxls9ObkinOdjaWdzY6D7q1mTiuXVdr9ekXB9TjEtLeLaY+fbSPX8AO0TkADUl3FC60sX8UBaCU/7CbBjsCvKSZ/eAsh5JK2D+/MLoNJL1RRkn2+ChkVeKI9Hw+niFQBMJO/+F8hLgnyO4xBFkUTjDpg2IlLAkSPdHzXj0sgSHmugCGSIEOW/3lFFtqD3VPFRQ=="]}'
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["create", "{\"name\": \"Car dealership\", \"transitions\": [{\"from\": 0, \"to\": 1, \"role\": \"Seller\", \"action\": \"Sell\"}, {\"from\": 1, \"to\": 2, \"role\": \"Buyer\", \"action\": \"Buy\"}]}", "Adam", "rrPpaH88z7CcTSGXsizSGAquwT2jBVW253ICAzQfF/wy28i/XOHC1FwQAWejYOLdHhHijzbZyPSbruB9dLx/n4UEXGc74BV35nYTiFHpdUNUouPW9yu9CoAGBlDV9XMeUQyZHd3YdOMN/+15r7FM8wPCry6H//eL5D/R5a3AXywrKGGFwU5XQGc9BjX1kgza/X2NvNp9XfxXTi3cnlcgKImM2fjGphRYbUSmEXNvszSce/lIMfdvCaR2ZDOhWzLDzkhLaD9BpBTyGsC5JoH56kVUuG5skKS8W6YYKL5uMHvaM5JjDs159uHIITVomUXRfbgTSjqYyjsaw0fh0B8pKQ=="]}'
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["start","{\"ssm\": \"Car dealership\", \"session\": \"deal20181201\", \"current\": 0, \"public\": \"Car dealer 2018 public\", \"private\": {\"Bob\": \"XXX\",\"Sam\": \"YYY\"}, \"roles\": {\"Buyer\": \"Bob\", \"Seller\": \"Sam\"}}", "Adam", "W4hOWGFalDISWCSRYIlfNnvr0ToS5RhLByxHZJorjnOzHDn0GNDstOaQmsQF924+uNEqZDaRirTanAQhVJhtOR2IRfCawWNYuYGIw4k3MjMlXnY5BM6f4btioTY3OQt5Ewg9vjq9BenMlrZtwMCTTinp8s3gbd46i3b0rrSoLFFbVQ6oZfe5WO7i/eUyxNcT3+BfQmoKovMFOH7d5lhZgj9ZiO6aCysAeHYxy+9TNsltUORObbMgqHbRH5f/pLktJAf+nl5tn7NDB0vV4772z9pOhjsHNm+1/MFhxx2//yHat0ypi4xjU3sM9TLcF+Npjh65IySYxU8qPfpbOGkqBQ=="]}' 

peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["admin", "Adam"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["user", "Bob"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["user", "Sam"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["ssm", "Car dealership"]}'
peer chaincode query -C ${CHANNEL_NAME} -n ${CHAINCODE} -c '{"Args":["session", "deal20181201"]}'

peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["perform", "Sell", "deal20181201", "Sam", "ABC"]}'
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["perform", "Buy", "deal20181201", "Bob", "EFG"]}'
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



