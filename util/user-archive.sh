#!/bin/bash

if [ $# -lt 2 ]
then
    echo "Usage: `basename $0` <user> <orga> [orderer_orga]"
    exit 1
fi

USER=$1
ORGA=$2

[[ "$3" ]] && ORDERER_ORGA_TLSCA="crypto-config/ordererOrganizations/$3/tlsca"

sed -i "/^cli_ORGA=.*$/d;/^cli_USER=.*$/d" .env
echo cli_ORGA=${ORGA} >> .env
echo cli_USER=${USER} >> .env

tar -cvzf user_${USER}_${ORGA}.tgz .env config docker-compose.yaml crypto-config/peerOrganizations/${ORGA}/users/${USER}@${ORGA} ${ORDERER_ORGA_TLSCA}

