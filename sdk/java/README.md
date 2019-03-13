
## Build docker

```
docker build -t civisblockchain/coop-rest .
docker push civisblockchain/coop-rest
```

## Run docker

```
echo ca__ADMIN=${ca__ADMIN} >> .env
echo ca__PASSWD=${ca__PASSWD} >> .env

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
curl -X GET "http://localhost:9090/ssm?cmd=query&fcn=list&args=ssm" -H  "accept: application/json"
```

```
curl -X GET "http://localhost:9090/ssm?cmd=query&fcn=admin&args=adrien" -H  "accept: application/json"
```