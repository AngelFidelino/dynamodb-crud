package com.aflr.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieDto {
  private Integer filmId;
  private String title;
  private int releaseYear;
  private String rated;
  private List<ActorDto> actors;
}
