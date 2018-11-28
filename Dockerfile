# Multi-stage build setup (https://docs.docker.com/develop/develop-images/multistage-build/)

# Stage 1 (to create a "build" image, ~140MB)
FROM openjdk:8-jdk-alpine3.8 AS builder
RUN java -version

COPY . /usr/src/myapp/
WORKDIR /usr/src/myapp/
RUN apk --no-cache add maven && mvn --version
RUN mvn package

# Stage 2 (to create a downsized "container executable", ~87MB)
FROM openjdk:8-jre-alpine3.8
WORKDIR /root/
COPY --from=builder /usr/src/myapp/target/image-server-0.1.0.jar .

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./image-server-0.1.0.jar"]