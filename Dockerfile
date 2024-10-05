FROM openjdk:21-jdk-slim

COPY target/bank-0.0.1-SNAPSHOT.jar /app/bank-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/bank-0.0.1-SNAPSHOT.jar"]