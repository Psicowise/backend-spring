# Primeira etapa: construir o projeto com Maven
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Segunda etapa: criar a imagem final apenas com o JRE e o JAR
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copia o JAR da primeira etapa
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]