package com.playlistColab.dtos;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpotifyPlaylistDto {
    private List<Item> items;
    private String next;

}
