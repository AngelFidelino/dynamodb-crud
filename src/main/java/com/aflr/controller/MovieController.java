package com.aflr.controller;

import com.aflr.dto.MovieDto;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/v1/movies")
@Validated
public interface MovieController {

  @GetMapping
  ResponseEntity<List<MovieDto>> getAllMovies(
      @RequestParam(name = "year", required = false) Integer year,
      @RequestParam(name = "title", required = false) String title);

  @GetMapping("/{key}")
  ResponseEntity<List<MovieDto>> getMovieById(
      @NotNull(message = "filmId must not be null") @PathVariable("key") Integer filmId,
      @RequestParam(name = "title", required = false) String title);

  @PostMapping
  ResponseEntity<MovieDto> insertMovie(@RequestBody MovieDto movie);

  @PutMapping
  ResponseEntity<MovieDto> updateMovie(@RequestBody MovieDto movie);

  @DeleteMapping
  ResponseEntity<Void> deleteMovie(@RequestBody MovieDto movie);
}
