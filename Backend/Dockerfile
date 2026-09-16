# ---- Aşama 1: Derleme (Build) ----
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Önce sadece pom.xml'i kopyala - bağımlılıklar değişmediyse Docker bu katmanı cache'ler
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Şimdi kaynak kodun tamamını kopyala ve derle
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Aşama 2: Çalıştırma (Runtime) ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Build aşamasından sadece üretilen .jar dosyasını al
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]