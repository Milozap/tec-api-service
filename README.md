# Overview
This repository contains a RESTful Api Service responsible for delegating data to Storage Service.
It runs on port 8082 by default.
Note for development: this service will not work properly without [eureka server](https://github.com/Milozap/tec-eureka-server) and [storage service](https://github.com/Milozap/tec-storage-service) running!

### Tech stack
- Language: Java 25 (Temurin)
- Frameworks/Libraries: Spring Boot 3.5.x, Spring Cloud 2025.0.x, Netflix Eureka Server
- Build tool: Gradle
- Testing: JUnit Platform via spring-boot-starter-test
- Code coverage: JaCoCo
- Containerization: Docker
- CI: GitHub Actions
- Service Discovery: Eureka Client
- Resilience4j
- Swagger UI

# Requirements
- [JDK 25](https://www.oracle.com/java/technologies/downloads/#jdk25-linux)
- Docker
- Gradle

## API Overview
- Actuator:
  - GET /actuator/health
  - GET /actuator/info
- OpenAPI/Swagger:
  - GET /v3/api-docs
  - GET /swagger-ui.html
  - GET /swagger-ui/index.html
- Movies API (requires Bearer JWT):
  - GET /movies?page={page}&size={size}
  - GET /movies/{id}
  - POST /movies
  - PUT /movies/{id}
  - DELETE /movies/{id}
  - GET /movies/dev/chaos?delay={ms}&errorRate={0..1}


# Getting started

1) Clone the repo
```shell
  git clone https://github.com/Milozap/tec-api-service
  cd tec-api-service
```

2) Build the project
```shell
  ./gradlew clean build
```

3) Run the application (local JVM)
```shell
  ./gradlew bootRun
```

4) Check health endpoint
- http://localhost:8082/actuator/health

### Docker

Build image:
```shell
  docker build -t api-service:local .
```

Run container:
```shell
docker run -p 8082:8082 --name api-service api-service:local
```

### How to Call the API (examples)
- Health:
  curl http://localhost:8082/actuator/health
- Swagger UI:
  http://localhost:8082/swagger-ui.html
- List movies (requires JWT):
  curl -H "Authorization: Bearer <token>" http://localhost:8082/movies
- Create movie (requires JWT):
  curl -X POST -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
    -d '{"title":"Interstellar","genre":"Sci-Fi","releaseYear":2014}' \
    http://localhost:8082/movies

### GitHub Actions (CI)

- The workflow .github/workflows/ci.yml:
    - Builds with Java 25 and Gradle
    - Runs tests and generates JaCoCo coverage
    - Uploads test reports on failure
    - Builds and (on non-PR events) pushes a Docker image to GitHub Container Registry (ghcr.io/milozap/tec-storage-service)
