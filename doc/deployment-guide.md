# Deployment Guide

This document outlines the steps to deploy this Java web application.

This information was last updated on November 11, 2023.

## Introduction

The following steps summarize the deployment at a high level

1. Create shaded JAR file
2. Create and build the Docker image
3. Upload the image to Docker Hub repository
4. Deploy the image using Render

## Docker and Javalin

There is an official Docker tutorial on the Javalin website:
- https://javalin.io/tutorials/docker

## Step 1: Shaded JAR

The first step is to create a JAR containing all application dependencies.
This can be accomplished using `maven-shade-plugin` and POM file configuration.
The shaded JAR will be created in the `target` folder of the project.

To more easily reference the JAR in the Dockerfile, the following tag may be used:

```xml
<finalName>raymoorexyz</finalName>
```

The following command is then used to create the shaded JAR:

```
mvn clean package
```

An alternative option is to use the `package` option in the IntelliJ Maven plugin.

## Step 2: Docker Image

The `dockerfile` in the project root will copy the shaded JAR once created, as shown below.

```
COPY target/raymoorexyz.jar /webapp.jar
```

The `src/env` and `src/main/webapp` directories will also be copied, as shown below.

```
COPY src/env src/env
COPY src/main/webapp src/main/webapp
```

Additionally, the Dockerfile will expose Port 8080 and specify the shaded JAR as the entry point.

```
FROM amazoncorretto:17
COPY src/env src/env
COPY src/main/webapp src/main/webapp
COPY target/raymoorexyz.jar /webapp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/webapp.jar"]
```

The following command will **build** the Docker image using the `dockerfile` above.

```
docker buildx build --platform linux/amd64 -t raymoorexyz . 
```

The `--platform` of `linux/amd64` is necessary if deploying on Render.

## Step 3: Docker Repository

Once the image is built, it must be tagged properly handler uploaded to Docker Hub.
- The local image has name `raymoorexyz` with tag `latest`
- The repository image has name `raymoore516/private` with tag `raymoorexyz-webapp`

```
docker tag raymoorexyz:latest raymoore516/private:raymoorexyz-webapp
```

To push the image to the Docker Hub container registry...

```
docker push raymoore516/private:raymoorexyz-webapp
```

## Step 4: Render Deployment

Once the Docker image is pushed to the Docker Hub repository, there are a variety of options.

Render allows deployment from a *private* container registry with supporting documentation:
- https://render.com/docs/deploy-an-image

Therefore, Render is the preferred method for this web application, using the registry below.

```
docker.io/raymoore516/private:raymoorexyz-webapp
```