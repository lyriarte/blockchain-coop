FABRIC_CLI_CHAINCODE_NAME	:= civisblockchain/fabric-cli-chaincode
FABRIC_CLI_CHAINCODE_IMG	:= ${FABRIC_CLI_CHAINCODE_NAME}:${VERSION}

SDK_REST_JAVA_NAME	:= civisblockchain/coop-rest-java
SDK_REST_JAVA_IMG	:= ${SDK_REST_JAVA_NAME}:${VERSION}

build: build-docker-fabric-cli-chaincode build-docker-sdk-rest-java

push: push-docker-fabric-cli-chaincode push-docker-sdk-rest-java

build-docker-fabric-cli-chaincode:
	@docker build -f docker/FabricCli_Dockerfile -t ${FABRIC_CLI_CHAINCODE_IMG} .

push-docker-fabric-cli-chaincode:
	@docker push ${FABRIC_CLI_CHAINCODE_IMG}

inspect-docker-fabric-cli-chaincode:
	@docker run -it ${FABRIC_CLI_CHAINCODE_IMG} /bin/bash

build-docker-sdk-rest-java:
	@docker build -f sdk/java/Dockerfile -t ${SDK_REST_JAVA_IMG} ./sdk/java

push-docker-sdk-rest-java:
	@docker push ${SDK_REST_JAVA_IMG}
