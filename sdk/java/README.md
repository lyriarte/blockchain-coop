
## Docker

```
docker build -t civisblockchain/coop-rest .
docker run -it -p 8080:8080 civisblockchain/coop-rest 
```

## Build project

```
./gradlew build
```

## String Rest api

Copy  config.json and crypto-config in coop-rest/src/main/resources/  
Edit coop-rest/src/main/resources/

```
coop:
  user:
    name: df82a3b46bda4183fb691fa9b57a39XX
    password: 121a59e3882a7e7344333772a79df5XX
    org: bclan
```

```
./gradlew coop-rest:bootRun
```
## Request Rest API

```
curl -X GET "http://localhost:8081/ssm?args=a&function=query" -H  "accept: application/json"
```

```
curl -X POST "http://localhost:8081/ssm" -H  "accept: application/json" -H  "Content-Type: application/json" -d "{  \"channel\": \"sandbox\",  \"chainid\": \"ex02\",  \"function\": \"invoke\",  \"args\": [    \"a\",\"b\",\"10\"  ]}"
```