FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw dependency:resolve

COPY src ./src

ENTRYPOINT ["./mvnw", "spring-boot:run"]
