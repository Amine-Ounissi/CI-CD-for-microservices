# OAuth2 token converter

### run on local

1. Run the following command to build jib optimized image to local docker deamon

```bash 
mvn package -Pdocker-image,local-client
```

2. Run the following command to run docker container

```bash 
docker run --rm -it -p 8081:8081/tcp -e REGISTRY_HOST=host.docker.internal -e IDENTITY_HOST=localhost -e IDENTITY_INTERNAL_HOST=host.docker.internal:8082 oauth2-token-converter:0.0.1-SNAPSHOT
```