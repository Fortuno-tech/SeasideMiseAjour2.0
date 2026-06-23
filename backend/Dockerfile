FROM openjdk:21-jdk-slim

WORKDIR /app

# Copier le pom.xml pour télécharger les dépendances (cache Docker)
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && \
    mvn dependency:go-offline && \
    apt-get remove -y maven && apt-get autoremove -y

# Copier le code source et build
COPY src ./src
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests && \
    apt-get remove -y maven && apt-get autoremove -y

# Copier le JAR
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]