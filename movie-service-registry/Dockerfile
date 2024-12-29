FROM openjdk:17-alpine
ARG JAR_FILE=target/movie-service-registry-1.0.jar
ADD ${JAR_FILE} movie-registry.jar
ENTRYPOINT ["java","-jar","/movie-registry.jar"]