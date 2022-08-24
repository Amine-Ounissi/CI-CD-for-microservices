# access manager

### run on local

1. Run the following command to build jib optimized image to local docker deamon

```bash 
mvn package -Pdocker-image,local-client
```

2. Run the following command to run docker container

```bash 
docker run --rm -it -p 9915:9915/tcp -e "REGISTRY_HOST=host.docker.internal" access-manager:0.0.1-SNAPSHOT
```