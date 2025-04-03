FROM eclipse-temurin:17
COPY ./target/hnotes-0.0.1-SNAPSHOT.jar /app/hnotes-notes.jar
COPY ./docker/entrypoint.sh /app/entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/app/entrypoint.sh"]
