FROM openjdk:17-alpine
ARG JAR_FILE=target/payment-service-1.0.jar
ADD ${JAR_FILE} paymentservice.jar
ENTRYPOINT ["java", "-jar", "/paymentservice.jar"]