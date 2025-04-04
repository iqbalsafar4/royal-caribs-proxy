FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/offshore-proxy-*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
