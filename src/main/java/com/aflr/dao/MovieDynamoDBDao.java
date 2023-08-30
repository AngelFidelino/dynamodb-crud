package com.aflr.dao;

import com.aflr.constant.DynamoDBConstant;
import com.aflr.entity.MovieEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetResultPage;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetResultPageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedResponse;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedResponse;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

@Repository
public class MovieDynamoDBDao implements MovieDao {

  private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
  private final DynamoDbClient dynamoDbClient;
  private final DynamoDbTable<MovieEntity> movieDynamoDbTable;

  public MovieDynamoDBDao(DynamoDbEnhancedClient dynamoDbEnhancedClient,
      DynamoDbClient dynamoDbClient) {
    this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
    this.dynamoDbClient = dynamoDbClient;
    movieDynamoDbTable = dynamoDbEnhancedClient.table(DynamoDBConstant.MOVIE_TABLE_NAME,
        TableSchema.fromBean(MovieEntity.class));
  }

  @Override
  public List<MovieEntity> getAllMovies() {
    Spliterator<MovieEntity> spliterator = movieDynamoDbTable.scan().items().stream().spliterator();
    return StreamSupport.stream(spliterator, false).collect(Collectors.toList());
  }

  public List<BatchGetResultPage> getBatchesByIds(List<String> keys) {
    //DynamoDbTable<MovieEntity> table = dynamoDbEnhancedClient.table("movie",TableSchema.fromBean(MovieEntity.class));

    ReadBatch.Builder<MovieEntity> readBatchBuilder = ReadBatch.builder(MovieEntity.class);
    readBatchBuilder.mappedTableResource(movieDynamoDbTable);
    keys.stream().forEach(key -> {
      readBatchBuilder.addGetItem(Key.builder().partitionValue(key).build());
    });
    ReadBatch readBatch = readBatchBuilder.build();

    BatchGetResultPageIterable iterable = dynamoDbEnhancedClient.batchGetItem(
        r -> r.addReadBatch(readBatch));
    return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
  }

  @Override
  public MovieEntity getMovieByKeys(MovieEntity movieEntity) {
    final Key key = Key.builder()
        .partitionValue(movieEntity.getFilmId())
        .sortValue(movieEntity.getTitle())
        .build();
    final MovieEntity item = movieDynamoDbTable.getItem(key);
    return item;
  }

  @Override
  public List<MovieEntity> getMovieByPartitionKey(Integer filmId) {
    Expression yearExpression = Expression.builder()
        .putExpressionName("filmId", String.valueOf(filmId))
        .build();

    QueryEnhancedRequest qEnhanced = QueryEnhancedRequest.builder()
        .filterExpression(yearExpression)
        .build();

    QueryConditional qConditional = QueryConditional.keyEqualTo(
        qc -> qc.partitionValue(filmId));

    final Expression filterOutMovieNoActors = Expression.builder()
        .expression("attribute_exists(actors)").build();

    QueryEnhancedRequest tableQuery = QueryEnhancedRequest.builder()
        .queryConditional(qConditional)
        .filterExpression(filterOutMovieNoActors)
        .build();
    //the query method might receive just a QueryConditional object
    return movieDynamoDbTable.query(tableQuery).items().stream().collect(Collectors.toList());
  }

  @Override
  public List<MovieEntity> getMoviesAfterYear(int year) {
    Map<String, AttributeValue> mapValues = new HashMap<>();
    mapValues.put(":releaseYear", AttributeValue.builder().n(String.valueOf(year)).build());

    ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
        .filterExpression(Expression.builder()
            .expression("releaseYear >= :releaseYear")
            .expressionValues(mapValues)
            .build())
        .build();

    return movieDynamoDbTable.scan(scanEnhancedRequest).items().stream()
        .collect(Collectors.toList());
  }

  @Override
  public List<MovieEntity> getMoviesByTitleUsingIndex(String movieTitle) {
    final DynamoDbIndex<MovieEntity> titleIndex = movieDynamoDbTable.index(
        DynamoDBConstant.GSI_MOVIE_TITLE);
    final Key key = Key.builder().partitionValue(movieTitle).build();
    final QueryConditional qc = QueryConditional.keyEqualTo(key);
    final SdkIterable<Page<MovieEntity>> result = titleIndex.query(
        q -> q.queryConditional(qc).attributesToProject("title", "releaseYear"));
    return result.stream().flatMap(o -> o.items().stream()).collect(Collectors.toList());
  }

  @Override
  public MovieEntity addMovie(MovieEntity movieEntity) {
    final PutItemEnhancedRequest<MovieEntity> movieEntityBuild = PutItemEnhancedRequest.builder(
        MovieEntity.class).item(movieEntity).build();
    final PutItemEnhancedResponse<MovieEntity> movieEntityPutItemEnhancedResponse = movieDynamoDbTable.putItemWithResponse(
        movieEntityBuild);

    return movieEntityPutItemEnhancedResponse.attributes() == null ? movieEntity
        : movieEntityPutItemEnhancedResponse.attributes();
  }

  @Override
  public MovieEntity updateMovie(MovieEntity movieEntity) {
    final UpdateItemEnhancedRequest<MovieEntity> updateEntityBuild = UpdateItemEnhancedRequest.builder(
        MovieEntity.class).item(movieEntity).build();
    final UpdateItemEnhancedResponse<MovieEntity> movieEntityUpdateItemEnhancedResponse = movieDynamoDbTable.updateItemWithResponse(
        updateEntityBuild);
    return movieEntityUpdateItemEnhancedResponse.attributes();
  }

  @Override
  public MovieEntity deleteMovie(MovieEntity movieEntity) {
    Key key = Key.builder().partitionValue(movieEntity.getFilmId())
        .sortValue(movieEntity.getTitle()).build();
    final DeleteItemEnhancedRequest deleteRequestBuild = DeleteItemEnhancedRequest.builder()
        .key(key)
        .build();
    final DeleteItemEnhancedResponse<MovieEntity> movieEntityDeleteItemEnhancedResponse = movieDynamoDbTable.deleteItemWithResponse(
        deleteRequestBuild);

    return movieEntityDeleteItemEnhancedResponse.attributes();
  }

  @Override
  public Map<String, AttributeValue> addMovieDefault() {
    HashMap<String, AttributeValue> itemValues = new HashMap<>();
    itemValues.put("filmId", AttributeValue.builder().n("0").build());
    itemValues.put("filmId", AttributeValue.builder().s("First movie").build());
    itemValues.put("releaseYear", AttributeValue.builder().n("2023").build());
    PutItemRequest putItemRequest = PutItemRequest.builder()
        .tableName("movie")
        .item(itemValues)
        .build();
    final PutItemResponse putItemResponse = dynamoDbClient.putItem(putItemRequest);

    return putItemResponse.attributes();
  }
}
