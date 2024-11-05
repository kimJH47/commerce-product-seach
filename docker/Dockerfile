FROM openjdk:17
RUN mkdir - p app
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar","-Duser.timezone=Asia/Seoul","-Dspring.profiles.active=prod","app.jar"]