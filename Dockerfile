FROM openjdk:21-jdk

# 애플리케이션 JAR 파일을 복사
COPY target/auth-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너가 시작될 때 실행할 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]
