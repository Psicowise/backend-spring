# Usa a imagem base do OpenJDK 17
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho no container
WORKDIR /app

# Copia o .jar compilado do seu projeto (mvn package) para dentro do container
COPY target/*.jar app.jar

# Expõe a porta 8080 (boa prática)
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]