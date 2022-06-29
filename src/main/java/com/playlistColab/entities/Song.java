package com.playlistColab.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.playlistColab.dtos.SongGetDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_songs")
@Builder
public class Song {
	
	@Id
	@NotBlank
	@Column(unique = true)
	@Builder.Default
	@Size(max = 255, min = 10)
	private String id = "init";

	@NotBlank
	@Size(max = 255, min = 1)
	private String title;
	@Size(max = 255, min = 1)
    private String thumbnailUrlLow;
	@Size(max = 255, min = 1)
    private String thumbnailUrlMedium;

    public static Song fromSongDto(SongGetDto songdto) {
        return Song.builder()
				.id(songdto.getVideoId())
				.title(songdto.getTitle())
				.thumbnailUrlLow(songdto.getThumbnailUrlLow())
				.thumbnailUrlMedium(songdto.getThumbnailUrlMedium())
				.build();
    }
}
