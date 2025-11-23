FROM eclipse-temurin:25-jdk-jammy AS builder

WORKDIR /app

COPY gradle ./gradle

COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew bootJar --no-daemon

RUN java -Djarmode=layertools -jar build/libs/*.jar extract

FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/application/ ./

RUN addgroup --system spring && adduser --system spring --ingroup spring && chown -R spring:spring /app

USER spring:spring

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD curl -f http://localhost:8761/actuator/health || exit 1

USER spring

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]