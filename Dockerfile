FROM maven:3.9-eclipse-temurin-8 AS build
WORKDIR /app
COPY . .
RUN mvn -q -B -DskipTests clean package

FROM eclipse-temurin:8-jre AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 3005
ENTRYPOINT ["java","-jar","/app/app.jar"]
