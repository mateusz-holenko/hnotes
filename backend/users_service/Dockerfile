FROM eclipse-temurin:17
COPY ./target/husers-0.0.1-SNAPSHOT.jar /app/hnotes-users.jar
EXPOSE 8090
ENTRYPOINT ["java", "-Xmx256m", "-jar", "/app/hnotes-users.jar" ]
