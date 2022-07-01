package com.playlistColab.dtos;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class youtubeSearchSongDto {
    @JsonProperty("snippet")
    private Snippet snippet;
    @JsonProperty("id")
    private ResourceId resourceId;

    public SongGetDto toSongGetDto() {
        return SongGetDto.builder()
                .title(snippet.getTitle())
                .videoId(resourceId.getVideoId())
                .thumbnailUrlLow(
                        Optional.ofNullable(snippet.getThumbnails()).map(thumnail -> thumnail.getLowThumbnail())
                                .map(lowThumnail -> lowThumnail.getUrl()).orElse(null))
                .thumbnailUrlMedium(
                        Optional.ofNullable(snippet.getThumbnails()).map(thumnail -> thumnail.getMediumThumbnail())
                                .map(mediumThumnail -> mediumThumnail.getUrl()).orElse(null))
                .songProvider(SongProviderEnum.YOUTUBE)
                .build();
    }
}
