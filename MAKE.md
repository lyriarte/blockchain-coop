# Make 

## Fabric cli chaincode
This is a docker image named civisblockchain/fabric-cli-chaincode and based on hyperledger/fabric-tools:1.4.0  
The image contains packaged chaincodes and tools needs to instantiate then in blockchain coop networks

It contains:
```
/opt/civis-blockchain/ssm/util      --> Bash script to instanciate, invoke and query the chaincode              
/opt/civis-blockchain/ssm/env       --> Env properties: CHAINCODE=ssm VERSION=0.4.2
/opt/civis-blockchain/ssm/ssm.pak   --> Packaged chaincode
```

### Build

```
make build-docker-fabric-cli-chaincode -e VERSION=0.2.1
```

### Push
```
make push-docker-fabric-cli-chaincode -e VERSION=0.2.1
```

### Inspect
```
make inspect-docker-fabric-cli-chaincode -e VERSION=0.2.1
```