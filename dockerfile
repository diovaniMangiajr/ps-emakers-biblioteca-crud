# Estágio 1: Compilação usando Maven e Java 17
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copia os arquivos de configuração do Maven e o código-fonte
COPY pom.xml .
COPY src ./src

# Executa o build limpando o cache e pulando os testes para acelerar o processo
RUN mvn clean package -DskipTests

# Estágio 2: Ambiente de Execução (Runtime)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia apenas o arquivo .jar compilado do estágio anterior
COPY --from=build /app/target/biblioteca-api-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta interna do container
EXPOSE 8080

# Comando para inicializar a API com flags de otimização da JVM
ENTRYPOINT ["java", "-jar", "app.jar"]