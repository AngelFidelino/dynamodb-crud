package com.aflr.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DynamoDBConstant {
  public static final String MOVIE_TABLE_NAME = "movie";
  public static final String GSI_MOVIE_TITLE = "gsi_movie_title";
}
