#!/bin/bash

if [ $# -ne 2 ]
then
    echo "Usage: `basename $0` <host> <orga>"
    exit 1
fi

HOST=$1
ORGA=$2
tar -cvzf peer_${HOST}_${ORGA}.tgz .env config docker-compose.yaml crypto-config/peerOrganizations/${ORGA}/peers/${HOST}.${ORGA}/msp crypto-config/peerOrganizations/${ORGA}/peers/${HOST}.${ORGA}/tls
