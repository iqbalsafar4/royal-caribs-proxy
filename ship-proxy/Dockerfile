FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/ship-proxy-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]