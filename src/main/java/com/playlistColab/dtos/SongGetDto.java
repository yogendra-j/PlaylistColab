package com.playlistColab.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.playlistColab.entities.Song;

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

	public static List<SongGetDto> fromSongs(Set<Song> songs) {
        List<SongGetDto> songGetDtos = new ArrayList<>();
        songs.forEach(song -> songGetDtos.add(SongGetDto.fromSong(song)));
        return songGetDtos;
	}
    public static SongGetDto fromSong(Song song) {
        return SongGetDto.builder()
                .title(song.getTitle())
                .thumbnailUrlLow(song.getThumbnailUrlLow())
                .thumbnailUrlMedium(song.getThumbnailUrlMedium())
                .videoId(song.getId())
                .build();
    }

}
