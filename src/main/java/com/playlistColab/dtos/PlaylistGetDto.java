package com.playlistColab.dtos;

import java.util.List;

import com.playlistColab.entities.Playlist;
import com.playlistColab.entities.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistGetDto {
    
    private Long id;
    
    private String name;
    
    private List<SongGetDto> songs;

    private User user;

	public static PlaylistGetDto fromPlaylist(Playlist playlist) {
		return PlaylistGetDto.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .user(playlist.getUser())
                .build();
	}
}
