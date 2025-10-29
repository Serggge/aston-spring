FROM amazoncorretto:17-alpine-jdk
COPY target/*.jar app.jar
RUN apk add --no-cache curl
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]