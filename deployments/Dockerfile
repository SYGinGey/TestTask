FROM maven:3.9.6-eclipse-temurin-21

WORKDIR usr/app

# Copy only pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

CMD ["mvn", "clean", "test", "-Dtags=regress"]