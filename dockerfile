FROM openjdk:21-jdk-oracle as builder
LABEL stage=builder
# if build is intended to happen inside builder stage then source must be present in docker context
COPY ./ ./
RUN ./mvnw clean package
ARG PROJECT=crust-service
RUN mv ${PROJECT}/target/*.jar application.jar
# this is using local build output so no actually no build happens in builder stage
# COPY ${PROJECT}/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:21-jdk-oracle
COPY --from=builder dependencies ./
COPY --from=builder spring-boot-loader ./
COPY --from=builder application ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]