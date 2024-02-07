FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/order-orchestrator-*.jar /app/order-orchastrator.jar
EXPOSE 9083
CMD ["java", "-jar", "order-orchastrator.jar"]