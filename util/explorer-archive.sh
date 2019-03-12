#!/bin/bash

if [ $# -ne 3 ]
then
    echo "Usage: `basename $0` <ORGA> <USER> <peer0:org0,peer1:org0,peerx:orgx...> "
    echo "Example: `basename $0` pr-bc1.civis-blockchain.org User1 'peer0:pr-bc1.civis-blockchain.org,peer1:pr-bc1.civis-blockchain.org' "
    exit 1
fi

ORGA=$1
USER=$2
ORGAS_PEERS=$(echo $3 | tr "," "\n")

CP_ARS=""
for ORGA_PEER in $ORGAS_PEERS
do
    IFS=':' read -ra PO <<< "${ORGA_PEER}"
    CP_ARS="$CP_ARS ./crypto-config/peerOrganizations/${PO[1]}/peers/${PO[0]}.${PO[1]}/tls/ca.crt";
done


tar -cvzf explorer_${HOST}_${ORGA}.tgz .env docker-compose.yaml explorer-config \
    $CP_ARS \
    crypto-config/peerOrganizations/${ORGA}/ca/ca.${ORGA}-cert.pem \
    crypto-config/peerOrganizations/${ORGA}/users/${USER}@${ORGA}/msp/keystore \
    crypto-config/peerOrganizations/${ORGA}/users/${USER}@${ORGA}/msp/signcerts
