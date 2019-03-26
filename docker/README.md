


```
cd bclan
docker build --build-arg COOP_PEER=peer0 \
    --build-arg COOP_CA_DOMAIN=bclan \
    --build-arg COOP_PEER_MSP=BlockchainLANCoopMSP \
    --build-arg COOP_PEER_DOMAIN=bc-coop.bclan \
    -f ../docker/Peer_Dockerfile \
    -t civisblockchain/bclan-peer0 .
```

```
cd bclan
docker build \
    --build-arg COOP_CA_HOSTNAME=ca \
    --build-arg COOP_PEER_DOMAIN=bc-coop.bclan \
    -f ../docker/Ca_Dockerfile \
    -t civisblockchain/bclan-ca .
```

```
cd bclan
docker build \
    --build-arg COOP_CA_DOMAIN=bclan \
    --build-arg COOP_ORDERER_HOSTNAME=orderer \
    -f ../docker/Orderer_Dockerfile \
    -t civisblockchain/bclan-orderer .
```



