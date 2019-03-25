#!/usr/bin/env bash

source /opt/blockchain-coop/env
peer chaincode query -C ${CHANNEL} -n ex02 -c '{"Args":["query","a"]}'
