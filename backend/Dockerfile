FROM eclipse-temurin:21-jdk

WORKDIR /app

# 1. Copier pom.xml pour les dépendances (cache)
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && \
    mvn dependency:go-offline && \
    apt-get remove -y maven && apt-get autoremove -y

# 2. Copier le code source ET build
COPY src ./src
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests && \
    apt-get remove -y maven && apt-get autoremove -y

# 3. Copier le JAR
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]