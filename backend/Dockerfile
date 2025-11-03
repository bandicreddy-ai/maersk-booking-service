# ---------- Build ----------
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# ---------- Run ----------
FROM eclipse-temurin:17-jre
ENV EXTERNAL_BASE_URL=http://wiremock:8080
WORKDIR /opt/app
COPY --from=build /app/target/booking-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
