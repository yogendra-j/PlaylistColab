package com.playlistColab.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class youtubeSongDto {

    @JsonProperty("snippet")
    private Snippet snippet;

    public SongGetDto toSongGetDto() {
        return SongGetDto.builder()
                .title(snippet.getTitle())
                .thumbnailUrlLow(snippet.getThumbnails().getLowThumbnail().getUrl())
                .thumbnailUrlMedium(snippet.getThumbnails().getMediumThumbnail().getUrl())
                .videoId(snippet.getResourceId().getVideoId())
                .build();
    }

}
