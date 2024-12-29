FROM openjdk:17-alpine
ARG JAR_FILE=target/booking-service-1.0.jar
ADD ${JAR_FILE} booking-service.jar
ENTRYPOINT ["java","-jar","/booking-service.jar"]