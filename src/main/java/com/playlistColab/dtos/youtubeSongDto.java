package com.playlistColab.dtos;

import java.util.Optional;

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
                .thumbnailUrlLow(
                        Optional.ofNullable(snippet.getThumbnails()).map(thumnail -> thumnail.getLowThumbnail())
                                .map(lowThumnail -> lowThumnail.getUrl()).orElse(null))
                .thumbnailUrlMedium(
                        Optional.ofNullable(snippet.getThumbnails()).map(thumnail -> thumnail.getMediumThumbnail())
                                .map(mediumThumnail -> mediumThumnail.getUrl()).orElse(null))
                .videoId(snippet.getResourceId().getVideoId())
                .build();
    }

}
