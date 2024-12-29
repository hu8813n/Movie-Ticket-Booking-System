FROM openjdk:17-alpine
ARG JAR_FILE=target/api-gateway-1.0.jar
ADD ${JAR_FILE} apigateway.jar
ENTRYPOINT ["java","-jar","/apigateway.jar"]