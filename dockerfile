FROM openjdk:21-jdk-oracle as builder
ARG PROJECT=crust-service
# if build is intended to happen inside builder stage then source must be excluded in dockerignore file
# RUN ./mvnw clean package -f ${PROJECT}/pom.xml
COPY ${PROJECT}/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:21-jdk-oracle
COPY --from=builder dependencies snapshot-dependencies spring-boot-loader application ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]