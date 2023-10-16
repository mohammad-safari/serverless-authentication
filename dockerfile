FROM openjdk:21-jdk-oracle as builder
LABEL stage=builder
ARG PROJECT=crust-service
# if build is intended to happen inside builder stage then source must be present in docker context
COPY ./ ./
RUN ./mvnw clean package
RUN mv ${PROJECT}/target/*.jar application.jar
# this is using local build output so no actually no build happens in builder stage
# COPY ${PROJECT}/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:21-jdk-oracle
COPY --from=builder dependencies snapshot-dependencies spring-boot-loader application ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]