FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/payment-service-0.0.1-SNAPSHOT.jar /app/payment-service.jar
EXPOSE 9082
CMD ["java", "-jar", "payment-service.jar"]