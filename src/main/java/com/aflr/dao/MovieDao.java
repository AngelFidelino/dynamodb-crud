package com.aflr.dao;

import com.aflr.entity.MovieEntity;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public interface MovieDao {

  List<MovieEntity> getAllMovies();

  MovieEntity getMovieByKeys(MovieEntity movieEntity);

  List<MovieEntity> getMovieByPartitionKey(Integer filmId);

  List<MovieEntity> getMoviesAfterYear(int year);

  List<MovieEntity> getMoviesByTitleUsingIndex(String movieTitle);

  MovieEntity addMovie(MovieEntity movieEntity);

  MovieEntity updateMovie(MovieEntity movieEntity);

  MovieEntity deleteMovie(MovieEntity movieEntity);

  Map<String, AttributeValue> addMovieDefault();


}
