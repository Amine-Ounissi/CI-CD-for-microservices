# registry

### run on local

1. Run the following command to build jib optimized image to local docker deamon

```bash 
mvn package -Pdocker-image,local-client
```

2. Run the following command to run docker container

If docker network is not created

```bash 
docker network create -d bridge registry-local
```

```bash 
docker container run --network registry-local --name registry -p 8080:8080/tcp registry:0.0.1-SNAPSHOT
```