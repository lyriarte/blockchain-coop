FABRIC_CLI_CHAINCODE_NAME	:= civisblockchain/fabric-cli-chaincode
FABRIC_CLI_CHAINCODE_IMG	:= ${FABRIC_CLI_CHAINCODE_NAME}:${VERSION}

build: build-docker-fabric-cli-chaincode

push: puch-docker-fabric-cli-chaincode

build-docker-fabric-cli-chaincode:
	@docker build -f docker/FabricCli_Dockerfile -t ${FABRIC_CLI_CHAINCODE_IMG} .

push-docker-fabric-cli-chaincode:
	@docker push ${FABRIC_CLI_CHAINCODE_IMG}

inspect-docker-fabric-cli-chaincode:
	@docker run -it ${FABRIC_CLI_CHAINCODE_IMG} /bin/bash


