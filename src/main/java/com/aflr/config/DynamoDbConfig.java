package com.aflr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {
//    @Value("${aws.access-key}") String accessKey;
  //  @Value("${aws.secret-key}") String secretKey;
    @Bean
    public DynamoDbClient getDynamoDbClient() {
        //AwsCredentials awsCredentials = AwsBasicCredentials.create("", "");

        //StaticCredentialsProvider staticCredentialsProvider
          //      = StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKey, secretKey, secToken));

        Region region = Region.US_EAST_1;
        return DynamoDbClient.builder()
                .region(region)
                //.credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getDynamoDbClient())
                .build();
    }
}
