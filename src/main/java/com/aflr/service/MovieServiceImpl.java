package com.aflr.service;

import com.aflr.dao.MovieDao;
import com.aflr.dto.ActorDto;
import com.aflr.dto.MovieDto;
import com.aflr.entity.ActorEntity;
import com.aflr.entity.MovieEntity;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class MovieServiceImpl implements MovieService {

  private MovieDao movieDao;

  public MovieServiceImpl(MovieDao movieDao) {
    this.movieDao = movieDao;
  }

  @Override
  public List<MovieDto> getAllMovies() {
    return movieDao.getAllMovies().stream().map(this::mapMovieDtoFrom)
        .collect(Collectors.toList());
  }

  @Override
  public List<MovieDto> getMovieByKeys(Integer key, String sortKey) {
    MovieDto movieDto = new MovieDto();
    movieDto.setFilmId(key);
    movieDto.setTitle(sortKey);

    final MovieEntity movieEntity = movieDao.getMovieByKeys(mapMovieEntityFrom(movieDto));

    return mapMovieDtosFrom(List.of(movieEntity));
  }

  @Override
  public List<MovieDto> getMovieByPartitionKey(Integer filmId) {
    List<MovieEntity> movies = movieDao.getMovieByPartitionKey(filmId);
    return mapMovieDtosFrom(movies);
  }

  @Override
  public List<MovieDto> getMoviesAfterYear(int year) {
    List<MovieEntity> movieEntities = movieDao.getMoviesAfterYear(year);
    return mapMovieDtosFrom(movieEntities);
  }

  @Override
  public List<MovieDto> getMoviesByTitleUsingIndex(String movieTitle) {
    List<MovieEntity> movieEntities = movieDao.getMoviesByTitleUsingIndex(movieTitle);
    return mapMovieDtosFrom(movieEntities);
  }

  @Override
  public MovieDto addMovie(MovieDto movie) {
    final MovieEntity movieEntity = movieDao.addMovie(mapMovieEntityFrom(movie));
    return mapMovieDtoFrom(movieEntity);
  }

  @Override
  public MovieDto updateMovie(MovieDto dto) {
    final MovieEntity movieEntity = movieDao.updateMovie(mapMovieEntityFrom(dto));
    return mapMovieDtoFrom(movieEntity);
  }

  @Override
  public void deleteMovie(MovieDto dto) {
    movieDao.deleteMovie(mapMovieEntityFrom(dto));
  }

  private MovieDto mapMovieDtoFrom(MovieEntity entity) {
    MovieDto movieDto = new MovieDto();
    BeanUtils.copyProperties(entity, movieDto);

    List<ActorDto> actors = Stream.ofNullable(entity.getActors()).flatMap(Collection::stream)
        .map(a -> {
          ActorDto actorDto = new ActorDto();
          BeanUtils.copyProperties(a, actorDto);
          return actorDto;
        }).collect(Collectors.toList());
    movieDto.setActors(actors);

    return movieDto;
  }

  private MovieEntity mapMovieEntityFrom(MovieDto dto) {
    MovieEntity movieEntity = new MovieEntity();
    BeanUtils.copyProperties(dto, movieEntity);

    List<ActorEntity> actors = Stream.ofNullable(dto.getActors()).flatMap(Collection::stream)
        .map(a -> {
          ActorEntity actorEntity = new ActorEntity();
          BeanUtils.copyProperties(a, actorEntity);
          return actorEntity;
        }).collect(Collectors.toList());
    movieEntity.setActors(actors);

    return movieEntity;
  }

  private List<MovieDto> mapMovieDtosFrom(List<MovieEntity> entities) {
    return entities.stream().map(e -> {
      MovieDto movieDto = new MovieDto();
      BeanUtils.copyProperties(e, movieDto);

      List<ActorDto> actors = Stream.ofNullable(e.getActors()).flatMap(Collection::stream)
          .map(a -> {
            ActorDto actorDto = new ActorDto();
            BeanUtils.copyProperties(a, actorDto);
            return actorDto;
          }).collect(Collectors.toList());
      movieDto.setActors(actors);

      return movieDto;
    }).collect(Collectors.toList());
  }

}