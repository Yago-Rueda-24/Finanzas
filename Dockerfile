# Etapa de build con Maven
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar el pom y las fuentes
COPY pom.xml .
COPY src ./src

# Compilar el JAR (sin tests para más velocidad)
RUN mvn clean package -DskipTests

# Imagen final más liviana con solo el JAR
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/Finanzas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
