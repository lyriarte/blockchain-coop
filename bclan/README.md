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
export VERSION=53b6d3d
peer chaincode install -n ${CHAINCODE} -v ${VERSION} -p blockchain-coop/ssm/
```

```
export CHANNEL_NAME=sandbox
export CHAINCODE=ssm
export VERSION=53b6d3d
peer chaincode instantiate -o orderer.bclan:7050 --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -C ${CHANNEL_NAME} -n ${CHAINCODE} -v ${VERSION} -c '{"Args":["init","[{\"name\": \"lyr-xps13\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxR0XhIzP0S9WTx7giz0iXqMhuwrjiai7GX8esPKuKMKQuGej5xTpKrfAf6/RtVRNPV3PQy92NqGXk+35nQVnGJU/GEpq86SnRrWWxVSqQR5Nh87DxbR3eoAwcKLFymsixJoWvpm/DU5Ut+Iuqy4Zla2zM5gS62/xlv03VJWVBPFN99pBybPWw0WnRbpnGFIpgDtyMjaE4U48Lmq8wesQ6c2RSXSE/HC76DOhmNKAbgkBnpMxvgW1AGUCJfB4KfutOkLb0OOHIRUeJv+FySwIeXyMh2o3xUQCHWKxSN3Rawg1aJBy2wj1jR9yUAwraLIUzguTaLDUvVH/4eKRGSryzwIDAQAB\"}]"]}' -P "OR ('BlockchainLANCoopMSP.member')"
```

```
peer chaincode upgrade -o orderer.bclan:7050 --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -C ${CHANNEL_NAME} -n ${CHAINCODE} -v ${VERSION} -c '{"Args":["init","[{\"name\": \"lyr-xps13\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxR0XhIzP0S9WTx7giz0iXqMhuwrjiai7GX8esPKuKMKQuGej5xTpKrfAf6/RtVRNPV3PQy92NqGXk+35nQVnGJU/GEpq86SnRrWWxVSqQR5Nh87DxbR3eoAwcKLFymsixJoWvpm/DU5Ut+Iuqy4Zla2zM5gS62/xlv03VJWVBPFN99pBybPWw0WnRbpnGFIpgDtyMjaE4U48Lmq8wesQ6c2RSXSE/HC76DOhmNKAbgkBnpMxvgW1AGUCJfB4KfutOkLb0OOHIRUeJv+FySwIeXyMh2o3xUQCHWKxSN3Rawg1aJBy2wj1jR9yUAwraLIUzguTaLDUvVH/4eKRGSryzwIDAQAB\"}]"]}' -P "OR ('BlockchainLANCoopMSP.member')"
```

```
export CHANNEL_NAME=sandbox
export CHAINCODE=ssm
export VERSION=53b6d3d

peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["register", "{\"name\": \"John Doe\", \"pub\": \"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzIhnIdLp2lcuvdVaFw22o8GFZdnkixm2s9VcCfa/v2IFvk73gGNP7euFnVY125vq1lHtd2mzFflixDj8F54ru9bGUOew1GaMBgYly09shYSk/SjkjyDDKLK8baWeWGU2OyiqV+zkLEJi++06xBO5ihUwrgdUFU8iGq7QdqH8DwVU3TFs5Y6EMRBIh4CkD1Vmbxb7r1704G574suErAbHcOQF6jLD37AGashuiUcrIt9Cm7+06fm72j9xlYA+CxbQnkPeOqU77zXq5AVH9XMf0qGAIfj+/3YDz1rhnM0SBzKfk3UHNp41k1aVUIKc/JBWYvgcl4ve8upLf7miLyZCawIDAQAB\"}", "lyr-xps13", "XYZ"]}'
peer chaincode invoke -o orderer.bclan:7050 -C ${CHANNEL_NAME} -n ${CHAINCODE} --tls --cafile /etc/hyperledger/orderer/tlsca.bclan-cert.pem -c '{"Args":["register", "{\"name\": \"Joe Black\", \"pub\": \"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2EvY4JUtnI68BOdsc/Q4WrRt0rh5ab6qKWAYkPIny6YCgggi69Z3Y5H+SY3OO/YcEk3g8d0EEx71vPxcq5FFaPc7VlmsI79I4m2YBB5mRzps72K23nf0Htwb7klJRoDTdmv/fjeOYhrQJHTzcymUBUZJKCxcclu+NY3EPjWl0t9+I1XF46gaLABwPlZJydPRhRzeVuJWv/IycxRS7iVjt8kJnicFQcvwPW9sIz+WTyIWrnOi+l6yjg8ImeRu7UvYErMHexlhYwZFeUULTtSwIQKrngL59PKNh7u1n+oAcpAhv2cnVxrLE7859p+mlDOzGhZwzCtNNaTDJ1+DA+uMgwIDAQAB\"}", "lyr-xps13", "XYZ"]}'
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



