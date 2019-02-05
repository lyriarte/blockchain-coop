#!/bin/bash

if [ $# -ne 1 ]
then
    echo "Usage: `basename $0` <orga>"
    exit 1
fi

ORGA=$1
tar -cvzf ca_${ORGA}.tgz .env config docker-compose.yaml crypto-config/peerOrganizations/${ORGA}/ca crypto-config/peerOrganizations/${ORGA}/tlsca
