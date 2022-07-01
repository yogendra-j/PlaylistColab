package com.playlistColab.dtos;

import java.util.List;

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
public class YoutubeSearchResult {
    @JsonProperty("items")
    private List<youtubeSearchSongDto> songs;
    @JsonProperty("nextPageToken")
    private String nextPageToken;
}
