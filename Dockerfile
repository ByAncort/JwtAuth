# Etapa 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (layer cacheado)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el JAR desde la etapa de build
COPY --from=builder /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]