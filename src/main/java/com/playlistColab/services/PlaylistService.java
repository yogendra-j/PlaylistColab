package com.playlistColab.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.playlistColab.dtos.AddSongDto;
import com.playlistColab.dtos.SongGetDto;
import com.playlistColab.dtos.SongProviderEnum;
import com.playlistColab.entities.Playlist;
import com.playlistColab.entities.Song;
import com.playlistColab.entities.User;
import com.playlistColab.exceptions.ResourceNotFoundException;
import com.playlistColab.repositories.PlaylistRepository;
import com.playlistColab.repositories.SongRepository;

@Service
public class PlaylistService {
	@Autowired
	PlaylistRepository playlistRepository;
	@Autowired
	SongRepository songRepository;
	@Autowired
	YoutubeService youtubeService; 

	public long createPlaylist(String playlistName, long userId) {
		Playlist playlist = Playlist
				.builder()
				.name(playlistName)
				.user(User.builder().id(userId).build())
				.build();
		return playlistRepository.save(playlist).getId();
	}

	public List<Playlist> findByUserId(long userId) {
		return playlistRepository.findByUserId(userId);
	}

	public Playlist findById(long playlistId) {
		return playlistRepository.findById(playlistId)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist with id " + playlistId + " not found."));
	}

	public void deleteById(long playlistId) {
		playlistRepository.deleteById(playlistId);
	}

	public Playlist addSongToPlaylist(long playlistId, AddSongDto addSongDto, String username) {
		addSongDto.setSongs(convertAllToYoutube(addSongDto.getSongs(), username));
		List<Song> songsInDb = songRepository
				.findAllById(addSongDto.getSongs().stream().map(s -> s.getVideoId()).collect(Collectors.toList()));
		List<Song> songsNeededToAddInDb = addSongDto.getSongs().stream().filter(songdto -> !songsInDb.stream()
				.anyMatch(song -> song.getId().equals(songdto.getVideoId()))).map(songdto -> Song.fromSongDto(songdto))
				.collect(Collectors.toList());
		songsInDb.addAll(songRepository.saveAllAndFlush(songsNeededToAddInDb)); //now all songs available in db
		Playlist playlist = findById(playlistId);
		playlist.getSongs().addAll(songsInDb);
		return playlistRepository.save(playlist);
	}

	public List<SongGetDto> convertAllToYoutube(List<SongGetDto> songs, String username){
		List<CompletableFuture<SongGetDto>> completableFutures = new ArrayList<>();
		songs.forEach(song -> {
			if (song.getSongProvider() == SongProviderEnum.YOUTUBE){
				completableFutures.add(CompletableFuture.supplyAsync(() -> song));
			} else {
				
				completableFutures.add(youtubeService.convertSpotifySongToYoutube(song.getSongQuery(), username));
			}
		});
		return completableFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());



	}

}
