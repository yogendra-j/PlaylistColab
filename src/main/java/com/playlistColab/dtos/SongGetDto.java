package com.playlistColab.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongGetDto {
    private String title;
    private String thumbnailUrlLow;
    private String thumbnailUrlMedium;
    private String videoId;

}
