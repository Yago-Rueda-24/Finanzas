# Imagen base con Java 21
FROM eclipse-temurin:21-jre

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR compilado desde tu máquina al contenedor
COPY target/Finanzas-0.0.1-SNAPSHOT.jar app.jar

# Puerto en el que corre Spring Boot
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
