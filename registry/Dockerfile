FROM openjdk:11

EXPOSE 8080

ADD ./target/registry-classes.jar amine-classes-*.jar

ADD ./target/registry.jar amine-registry-*.jar
CMD ["java", "-jar", "amine-registry-*.jar"]
