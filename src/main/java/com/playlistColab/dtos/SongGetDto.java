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
    private SongProviderEnum songProvider;

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
                .songProvider(song.getSongProviderEnum())
                .build();
    }

    public static SongGetDto fromTracksSpotify(TracksSpotify tracksSpotify) {
        int numberOfImages = tracksSpotify.getAlbum().getImages().size();
        return SongGetDto.builder()
                .title(tracksSpotify.getName())
                .thumbnailUrlLow(tracksSpotify.getAlbum().getImages().get(numberOfImages - 1).getUrl())
                .thumbnailUrlMedium(tracksSpotify.getAlbum().getImages().get(numberOfImages > 1 ? numberOfImages - 2 : 0).getUrl())
                .videoId(tracksSpotify.getId())
                .songProvider(SongProviderEnum.SPOTIFY)
                .build();
    }

}
