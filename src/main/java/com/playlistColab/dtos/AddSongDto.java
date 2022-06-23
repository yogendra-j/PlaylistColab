package com.playlistColab.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddSongDto {
    private List<String> songIds;
    private SongProviderEnum songProvider;
}
