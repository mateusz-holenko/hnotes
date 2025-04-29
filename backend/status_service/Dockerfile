FROM eclipse-temurin:17
COPY ./target/status_service-0.0.1-SNAPSHOT.jar /app/hnotes-status.jar
COPY ./docker/entrypoint.sh /app/entrypoint.sh
EXPOSE 8100
ENTRYPOINT ["/app/entrypoint.sh"]
