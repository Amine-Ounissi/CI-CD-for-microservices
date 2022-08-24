FROM openjdk:11
ADD ./oauth2-token-converter/target/oauth2-token-converter-*.jar amine-auth-*.jar
EXPOSE 8080



ADD ./platform-starter-token-converter/target/platform-starter-token-converter-*.jar amine-token-*.jar
CMD ["java", "-jar", "amine-auth-*.jar"]
