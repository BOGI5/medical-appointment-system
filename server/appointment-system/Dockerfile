# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy the Maven wrapper and the pom.xml file to the container
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies early to take advantage of Docker layer caching
RUN ./mvnw dependency:go-offline

COPY . .

RUN ./mvnw clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
