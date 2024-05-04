FROM eclipse-temurin:17-alpine

WORKDIR /app

COPY target/*.jar /app/relatorio-api.jar

EXPOSE 8081

CMD ["java", "-jar", "relatorio-api.jar"]