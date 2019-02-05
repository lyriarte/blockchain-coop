#!/bin/bash

if [ $# -ne 2 ]
then
    echo "Usage: `basename $0` <host> <orga>"
    exit 1
fi

HOST=$1
ORGA=$2
tar -cvzf orderer_${HOST}_${ORGA}.tgz .env config docker-compose.yaml crypto-config/ordererOrganizations/${ORGA}/orderers/${HOST}.${ORGA}/msp crypto-config/ordererOrganizations/${ORGA}/orderers/${HOST}.${ORGA}/tls
