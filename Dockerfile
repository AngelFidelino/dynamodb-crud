FROM openjdk:11
ADD target/dynamodb-crud-1.0.jar dynamodb-crud-1.0.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "dynamodb-crud-1.0.jar"]