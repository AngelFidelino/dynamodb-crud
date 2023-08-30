package com.aflr.service;

import com.aflr.dto.MovieDto;
import java.util.List;

public interface MovieService {

  List<MovieDto> getAllMovies();

  List<MovieDto> getMovieByKeys(Integer key, String sortKey);

  List<MovieDto> getMovieByPartitionKey(Integer filmId);

  List<MovieDto> getMoviesAfterYear(int year);

  List<MovieDto> getMoviesByTitleUsingIndex(String movieTitle);

  MovieDto addMovie(MovieDto movie);

  MovieDto updateMovie(MovieDto dto);

  void deleteMovie(MovieDto dto);
}
