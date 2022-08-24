FROM quay.io/keycloak/keycloak:12.0.3

WORKDIR /opt/vds-identity

#ENV KEYCLOAK_USER admin
#ENV KEYCLOAK_PASSWORD admin
#ENV PROXY_ADDRESS_FORWARDING true
#ENV DB_VENDOR mysql
#ENV DB_ADDR mysql-valueds-002.mysql.database.azure.com
#ENV DB_PORT 3306
#ENV DB_DATABASE infra_keycloak
#ENV JDBC_PARAMS &useSSL=true&requireSSL=false&serverTimezone=UTC
#ENV DB_USER value@mysql-valueds-002
#ENV DB_PASSWORD Vds@2021

ENV KEYCLOAK_IMPORT /tmp/keycloak-export/infrastructure-realm.json
ENV KEYCLOAK_USER admin
ENV KEYCLOAK_PASSWORD admin
ENV PROXY_ADDRESS_FORWARDING true
ENV DB_VENDOR MYSQL
ENV DB_PORT 3306
ENV KEYCLOAK_IMPORT /tmp/keycloak-export/infrastructure-realm.json

# local configs
ENV DB_ADDR mysql
ENV DB_DATABASE keycloak
ENV DB_USER keycloak
ENV DB_PASSWORD keycloak

### vm configs
#ENV DB_ADDR mysql-valueds-002.mysql.database.azure.com
#ENV DB_DATABASE infra_keycloak
#ENV JDBC_PARAMS &useSSL=true&requireSSL=false&serverTimezone=UTC
#ENV DB_USER value@mysql-valueds-002
#ENV DB_PASSWORD Vds@2021

USER root

COPY json /tmp/keycloak-export

EXPOSE 8080

