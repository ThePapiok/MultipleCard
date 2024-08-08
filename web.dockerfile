FROM maven:3.9.8-eclipse-temurin-17 as build

WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/multipleCard.jar ./

EXPOSE 8080

CMD ["java", "-jar", "multipleCard.jar"]
