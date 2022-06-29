package com.playlistColab.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TracksSpotify {
    @JsonProperty("album")
    private AlbumDto album;
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private String id;
}
