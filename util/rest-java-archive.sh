# Peer1: ./crypto-config/peerOrganizations/${PO[1]}/peers/${PO[0]}.${PO[1]}/msp/tlscacerts/tlsca.${PO[1]}-cert.pem


#!/bin/bash

if [ $# -ne 4 ]
then
    echo "Usage: `basename $0`<ORDERER_ORGA> <ORGA> <USER> <peer0:org0,peer1:org0,peerx:orgx...> "
    echo "Example: `basename $0` or-bc1.chain-ops.net pr-bc1.civis-blockchain.org User1 'peer0:pr-bc1.civis-blockchain.org,peer1:pr-bc1.civis-blockchain.org' "
    exit 1
fi

ORDERER_ORGA=$1
ORGA=$2
USER=$3
ORGAS_PEERS=$(echo $4 | tr "," "\n")

CP_ARS=""
for ORGA_PEER in $ORGAS_PEERS
do
    IFS=':' read -ra PO <<< "${ORGA_PEER}"
    CP_ARS="$CP_ARS ./crypto-config/peerOrganizations/${PO[1]}/peers/${PO[0]}.${PO[1]}/msp/tlscacerts/tlsca.${PO[1]}-cert.pem";
done


tar -cvzf rest-java_${ORGA}.tgz .env config.json docker-compose.yaml \
    $CP_ARS \
    crypto-config/peerOrganizations/${ORGA}/users/${USER}@${ORGA}/tls/ca.crt \
    crypto-config/ordererOrganizations/${ORDERER_ORGA}/tlsca/tlsca.${ORDERER_ORGA}-cert.pem
