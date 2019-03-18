
## Build docker

```
docker build -t civisblockchain/coop-rest-java .
docker push civisblockchain/coop-rest-java
```

## Run docker

Exemple configuration to use ssm in BC1
```
echo ca__ADMIN=${ca__ADMIN} >> .env
echo ca__PASSWD=${ca__PASSWD} >> .env

echo ca__ORG=CivisBlockchain >> .env

echo endorsers=peer0:CivisBlockchain,peer1:CivisBlockchain >> .env
echo channel=sandbox >> .env
echo ccid=ssm >> .env

echo config_file=../../bc1/config.json >> .env
echo config_crypto=../../bc1/crypto-config >> .env

docker-compose -f docker-compose.yaml up
```

## Build project

```
./gradlew build
```

## Configuration

In dev mode configuration can be change in coop-rest/src/main/resources/application.yml

## Run

```
./gradlew coop-rest:bootRun
```

## Swagger ui

```
http://localhost:9090/swagger-ui.html
```

## Request Rest API


```
curl -X GET "http://localhost:9090/v2?fcn=list&args=ssm" -H  "accept: application/json"

```
```
curl -X GET "http://localhost:9090/v2?cmd=query&fcn=list&args=ssm" -H  "accept: application/json"
```

```
curl -X GET "http://localhost:9090/v2?cmd=query&fcn=admin&args=adrien" -H  "accept: application/json"
```