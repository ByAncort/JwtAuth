# JwtAuth - Microservicio de Autenticación

Microservicio Spring Boot para autenticación basada en JWT con características empresariales completas incluyendo monitoreo, seguridad y CI/CD automatizado.

## Características

- ✅ **Autenticación JWT** segura y escalable
- ✅ **Java 21** con las últimas características del lenguaje
- ✅ **Spring Boot 3** con configuración moderna
- ✅ **Base de datos MySQL** para producción y **H2** para testing
- ✅ **Monitoreo completo** con Prometheus y Grafana
- ✅ **Métricas Prometheus** expuestas vía Actuator
- ✅ **Dashboards Grafana** pre-configurados para JVM y Spring Boot
- ✅ **Logs estructurados** con Logstash
- ✅ **CI/CD completo** con GitHub Actions
- ✅ **Análisis de calidad** con SonarQube
- ✅ **Escaneo de seguridad** con Snyk y Trivy
- ✅ **Imágenes Docker** optimizadas y publicadas en GHCR

## Requisitos

- Java 21 o superior
- Maven 3.8+
- Docker y Docker Compose (para Prometheus y Grafana)
- MySQL 8+ (para producción)

## Ejecución Local

### 1. Iniciar Base de Datos

```bash
# Opción A: MySQL con Docker
docker run -d \
  --name mysql-dev \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=1290 \
  -e MYSQL_DATABASE=testdb \
  mysql:8

# Opción B: Usar H2 (en memoria) con perfil de desarrollo
# No requiere instalación adicional
```

### 2. Ejecutar la Aplicación

```bash
# Clonar el repositorio
git clone https://github.com/ByAncort/JwtAuth.git
cd JwtAuth

# Compilar y ejecutar con MySQL
mvn clean spring-boot:run

# O ejecutar con H2 para desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# O ejecutar tests
mvn clean verify
```

La aplicación estará disponible en `http://localhost:9001`

### 3. Iniciar Stack de Monitoreo

```bash
# Levantar Prometheus y Grafana
docker compose up -d

# Verificar que estén corriendo
docker compose ps
```

**URLs de acceso:**
- **Aplicación**: http://localhost:9001
- **Swagger UI**: http://localhost:9001/swagger-ui.html
- **Actuator Health**: http://localhost:9001/actuator/health
- **Métricas Prometheus**: http://localhost:9001/actuator/prometheus
- **Prometheus UI**: http://localhost:9091
- **Grafana**: http://localhost:3001 (admin/admin123)

## Monitoreo con Prometheus y Grafana

### Configuración de Prometheus

Prometheus está configurado para recolectar métricas cada 10 segundos del endpoint `/actuator/prometheus`:

```yaml
# monitoring/prometheus.yml
scrape_configs:
  - job_name: "jwt-auth-service"
    metrics_path: "/actuator/prometheus"
    static_configs:
      - targets: ["host.docker.internal:9001"]
```

### Dashboards de Grafana

Grafana viene pre-configurado con el datasource de Prometheus. Dashboards recomendados:

**Dashboard JVM Micrometer (ID: 11378)**
- Uso de memoria heap y non-heap
- Threads activos y daemon
- Garbage collection metrics
- CPU usage

**Importar dashboard:**
1. Accede a Grafana: http://localhost:3001
2. Ve a Dashboards → Import
3. Ingresa el ID: **11378**
4. Selecciona datasource: **Prometheus**
5. Click en **Import**

**Otros dashboards recomendados:**
- **4701**: JVM (Micrometer)
- **12900**: Spring Boot 2.1 System Monitor
- **6756**: Spring Boot Statistics

### Métricas Expuestas

La aplicación expone las siguientes métricas:

- **JVM**: Memoria, threads, garbage collection, classloaders
- **HTTP**: Request rate, latencia, errores por endpoint
- **Sistema**: CPU, disk space, uptime
- **Spring Boot**: Beans, data sources, cache
- **Custom**: Métricas personalizadas de negocio

**Queries útiles en Prometheus:**

```promql
# Request rate por segundo
rate(http_server_requests_seconds_count{application="jwt-auth-service"}[1m])

# Latencia promedio (ms)
rate(http_server_requests_seconds_sum{application="jwt-auth-service"}[1m]) 
/ 
rate(http_server_requests_seconds_count{application="jwt-auth-service"}[1m]) 
* 1000

# Uso de memoria heap
jvm_memory_used_bytes{application="jwt-auth-service",area="heap"}

# Rate de errores HTTP 5xx
rate(http_server_requests_seconds_count{application="jwt-auth-service",status=~"5.."}[1m])
```

## Ejecución con Docker

```bash
# Construir imagen localmente
docker build -t jwt-auth .

# Ejecutar contenedor
docker run -p 9001:9001 jwt-auth
```

O usar la imagen pre-construida desde GitHub Container Registry:

```bash
docker pull ghcr.io/byancort/jwt-auth:latest
docker run -p 9001:9001 ghcr.io/byancort/jwt-auth:latest
```

## Estructura del Proyecto

```
JwtAuth/
├── src/
│   ├── main/
│   │   ├── java/          # Código fuente Java
│   │   └── resources/     # Configuración y recursos
│   └── test/              # Tests unitarios y de integración
├── .github/
│   └── workflows/         # Pipelines CI/CD
├── monitoring/            # Configuración de monitoreo
│   ├── prometheus.yml     # Configuración de Prometheus
│   └── grafana/
│       └── provisioning/  # Datasources pre-configurados
├── docker-compose.yml     # Stack de Prometheus y Grafana
├── Dockerfile             # Configuración de imagen Docker
├── .dockerignore          # Exclusiones de build Docker
└── pom.xml               # Configuración Maven
```

## CI/CD Pipeline

El proyecto incluye un pipeline completo que se ejecuta en cada push y pull request:

### Jobs del Pipeline

1. **build-and-test**: Compila y ejecuta tests unitarios
2. **sonarqube-analysis**: Análisis de calidad de código
3. **security-scan**: Escaneo de vulnerabilidades con Snyk
4. **docker-build**: Construye y publica imagen Docker (solo en main)
   - Build y push a GHCR
   - Escaneo de seguridad con Trivy
   - Upload de resultados a GitHub Security

### Publicación de Imágenes

Las imágenes Docker se publican automáticamente en GitHub Container Registry cuando se mergea a la rama main:

- **Repository**: `ghcr.io/byancort/jwt-auth`
- **Tags**: `latest` y `main-{sha}`

### Seguridad

- **Escaneo de dependencias** con Snyk (umbral: high)
- **Escaneo de contenedores** con Trivy para vulnerabilidades críticas y altas
- **Resultados** subidos a GitHub Security tab
- **Análisis local** antes de push al registry

## Configuración

### Base de Datos

**Desarrollo (MySQL):**
```yaml
spring:
  datasource:
    url: jdbc:mysql://host.docker.internal:3306/testdb
    username: root
    password: 1290
```

**Testing (H2):**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
```

### Actuator y Métricas

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: jwt-auth-service
      environment: development
```

### Monitoreo

- **Actuator endpoints** disponibles en `/actuator`
- **Métricas Prometheus** en `/actuator/prometheus`
- **Health checks** en `/actuator/health`
- **Prometheus UI** en http://localhost:9091
- **Grafana dashboards** en http://localhost:3001

## Comandos Útiles

### Desarrollo
```bash
# Compilar sin tests
mvn clean install -DskipTests

# Ejecutar tests con cobertura
mvn clean test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html

# Análisis con SonarQube (requiere SONAR_TOKEN)
mvn clean verify sonar:sonar
```

### Docker Compose
```bash
# Levantar stack de monitoreo
docker compose up -d

# Ver logs
docker compose logs -f

# Ver logs de Prometheus
docker compose logs -f prometheus

# Ver logs de Grafana
docker compose logs -f grafana

# Reiniciar servicios
docker compose restart

# Detener servicios
docker compose down

# Detener y eliminar volúmenes
docker compose down -v
```

### Verificación de Métricas
```bash
# Health check
curl http://localhost:9001/actuator/health

# Todas las métricas
curl http://localhost:9001/actuator/metrics

# Métrica específica (ej: memoria)
curl http://localhost:9001/actuator/metrics/jvm.memory.used

# Formato Prometheus
curl http://localhost:9001/actuator/prometheus
```

## Desarrollo

### Plugins Maven Configurados

- **Spring Boot Maven Plugin**: Para empaquetar y ejecutar la aplicación
- **JaCoCo Plugin**: Para cobertura de código
- **SonarQube Plugin**: Para análisis de calidad
- **Surefire Plugin**: Configurado para Java 21

### Tests

```bash
# Ejecutar todos los tests
mvn clean test

# Ejecutar con cobertura
mvn clean test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html
```

### Métricas Personalizadas

Ejemplo de cómo agregar métricas personalizadas:

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class AuthService {
    private final Counter loginAttempts;
    
    public AuthService(MeterRegistry registry) {
        this.loginAttempts = Counter.builder("auth.login.attempts")
            .description("Total login attempts")
            .tag("status", "success")
            .register(registry);
    }
    
    public void login() {
        loginAttempts.increment();
        // Lógica de autenticación
    }
}
```

## Troubleshooting

### Prometheus no detecta la aplicación

1. Verifica que tu aplicación esté corriendo:
   ```bash
   curl http://localhost:9001/actuator/health
   ```

2. Verifica las métricas:
   ```bash
   curl http://localhost:9001/actuator/prometheus
   ```

3. Revisa los logs de Prometheus:
   ```bash
   docker compose logs prometheus
   ```

4. Verifica los targets en Prometheus UI:
   - Ve a: http://localhost:9091/targets
   - `jwt-auth-service` debe estar **UP**

### Grafana no puede conectarse a Prometheus

1. Verifica que ambos contenedores estén en la misma red:
   ```bash
   docker network inspect authjwt_monitoring
   ```

2. Prueba la conexión desde Grafana:
   ```bash
   docker exec -it grafana curl http://prometheus:9090/api/v1/targets
   ```

3. Reconfigura el datasource manualmente en Grafana:
   - URL: `http://prometheus:9090`
   - Click en "Save & Test"

## Contribución

1. Fork del repositorio
2. Crear feature branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

El pipeline CI/CD se ejecutará automáticamente validando los cambios.

## Badges

![Build Status](https://github.com/ByAncort/JwtAuth/actions/workflows/ci.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ByAncort_JwtAuth&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ByAncort_JwtAuth)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ByAncort_JwtAuth&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ByAncort_JwtAuth)


---

## Notas Técnicas

- La aplicación expone el puerto **9001** por defecto
- Prometheus expone el puerto **9091** (mapeado desde 9090)
- Grafana expone el puerto **3001** (mapeado desde 3000)
- Las imágenes Docker usan multi-stage build para optimizar tamaño
- El usuario `spring` sin privilegios de root se usa en producción
- Los logs están configurados en formato JSON para Logstash
- Las métricas son compatibles con Prometheus para monitoreo
- Grafana viene con datasource pre-configurado apuntando a Prometheus
- Los datos de Prometheus y Grafana persisten en volúmenes Docker

