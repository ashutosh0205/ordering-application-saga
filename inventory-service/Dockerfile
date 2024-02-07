FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/inventory-service-0.0.1-SNAPSHOT.jar /app/inventory-service.jar
EXPOSE 9081
CMD ["java", "-jar", "inventory-service.jar"]