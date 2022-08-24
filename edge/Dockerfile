FROM openjdk:11

EXPOSE 8080

ADD ./edge-filters/remove-request-cookie/target/remove-request-cookie-*.jar amine-remove-*.jar

ADD ./edge-filters/rewrite-path/target/rewrite-path-*.jar amine-rewrite-*.jar

ADD ./edge-filters/token-conversion/target/token-conversion-*.jar amine-conversion-*.jar

ADD ./edge-filters/validate-routes/target/validate-routes-*.jar amine-validate-*.jar

ADD ./edge/target/edge.jar amine-edge.jar
CMD ["java", "-jar", "amine-edge.jar"]


