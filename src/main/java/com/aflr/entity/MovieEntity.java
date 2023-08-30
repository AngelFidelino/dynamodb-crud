package com.aflr.entity;

import com.aflr.constant.DynamoDBConstant;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Setter
@Getter
@NoArgsConstructor
public class MovieEntity {

  private Integer filmId;
  private String title;
  @DynamoDBAttribute
  private int releaseYear;
  @DynamoDBAttribute
  private String rated;
  private List<ActorEntity> actors;

  @DynamoDbPartitionKey
  public Integer getFilmId() {
    return filmId;
  }

  @DynamoDbSortKey
  @DynamoDbSecondaryPartitionKey(indexNames = {DynamoDBConstant.GSI_MOVIE_TITLE})
  public String getTitle() {
    return title;
  }
}
