FROM maven:3.9-eclipse-temurin-21
WORKDIR /app
COPY pom.xml checkstyle.xml ./
COPY src ./src
ENV DB_URL=jdbc:postgresql://host.docker.internal:5432/auca_library_db
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres
CMD ["mvn", "-B", "test"]
