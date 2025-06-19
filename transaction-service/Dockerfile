FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace
COPY pom.xml .
COPY src src

RUN apk add --no-cache maven && \
    mvn clean package -DskipTests && \
    mkdir -p /layers && \
    java -Djarmode=layertools -jar target/*.jar extract --destination /layers


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && \
    adduser -S spring -G spring && \
    apk add --no-cache tzdata curl && \
    ln -sf /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime

COPY --from=builder --chown=spring:spring /layers/dependencies/ ./
COPY --from=builder --chown=spring:spring /layers/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /layers/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /layers/application/ ./

USER spring

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]