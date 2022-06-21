package com.playlistColab.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PlaylistCreateDto {
	@NotBlank
	@Size(max = 40, min = 1)
	private String name;
}
