FROM eclipse-temurin:17-alpine

WORKDIR /app

COPY target/*.jar /app/emiteai-backend.jar

EXPOSE 8080

CMD ["java", "-jar", "emiteai-backend.jar"]