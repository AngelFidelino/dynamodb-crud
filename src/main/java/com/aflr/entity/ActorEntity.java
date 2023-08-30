package com.aflr.entity;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
@Setter
@Getter
public class ActorEntity {

  private int actorId;
  private String name;
  private String nationality;
}
