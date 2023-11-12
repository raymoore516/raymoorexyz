FROM amazoncorretto:17
COPY src/env src/env
COPY src/main/webapp src/main/webapp
COPY target/raymoorexyz.jar /webapp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/webapp.jar"]