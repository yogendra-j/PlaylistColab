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
public class Snippet {
    @JsonProperty("title")
    private String title;
    @JsonProperty("thumbnails")
    private Thumbnails thumbnails;
    @JsonProperty("resourceId")
    private ResourceId resourceId;
}
