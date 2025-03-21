# Étape 1 : Base image avec Maven et JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS dev

# Définir le répertoire de travail
WORKDIR /app

# Copier tous les fichiers (pom.xml, src, etc.)
COPY . .

# Lancer Spring Boot en mode dev
CMD ["mvn", "spring-boot:run"]