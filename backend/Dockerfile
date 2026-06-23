FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copier le pom.xml et les sources
COPY pom.xml .
COPY src ./src

# Installer Maven, build, et copier le JAR
RUN apt-get update && \
    apt-get install -y maven && \
    mvn clean package -DskipTests

# Copier le JAR généré
RUN cp target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]