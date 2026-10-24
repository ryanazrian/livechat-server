FROM openjdk:8-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests clean package

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 3005
ENTRYPOINT ["java","-jar","/app.jar"]
