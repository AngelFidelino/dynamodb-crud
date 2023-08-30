package com.aflr.controller;

import com.aflr.dto.MovieDto;
import com.aflr.service.MovieService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieControllerImpl implements MovieController {

  private MovieService movieService;

  public MovieControllerImpl(MovieService movieService) {
    this.movieService = movieService;
  }

  @Override
  public ResponseEntity<List<MovieDto>> getAllMovies(Integer year, String title) {
    if (year != null) {
      return new ResponseEntity<>(movieService.getMoviesAfterYear(year), HttpStatus.OK);
    } else if (StringUtils.hasLength(title)) {
      return new ResponseEntity<>(movieService.getMoviesByTitleUsingIndex(title), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }
  }

  @Override
  public ResponseEntity<List<MovieDto>> getMovieById(Integer filmId, String title) {
    if (StringUtils.hasLength(title)) {
      return ResponseEntity.ok(movieService.getMovieByKeys(filmId, title));
    }
    return ResponseEntity.ok(movieService.getMovieByPartitionKey(filmId));
  }

  @Override
  public ResponseEntity<MovieDto> insertMovie(MovieDto movie) {
    return new ResponseEntity<>(movieService.addMovie(movie), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<MovieDto> updateMovie(MovieDto movie) {
    return new ResponseEntity<>(movieService.updateMovie(movie), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> deleteMovie(MovieDto movie) {
    movieService.deleteMovie(movie);
    return new ResponseEntity<Void>(HttpStatus.OK);
  }
}
