FROM amazoncorretto:21

WORKDIR /app

COPY target/webframework.jar app.jar

ENV PORT=8080
ENV APP_ENV=production

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
