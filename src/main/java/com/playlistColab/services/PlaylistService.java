package com.playlistColab.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.playlistColab.entities.Playlist;
import com.playlistColab.entities.User;
import com.playlistColab.exceptions.ResourceNotFoundException;
import com.playlistColab.repositories.PlaylistRepository;

@Service
public class PlaylistService {
	@Autowired PlaylistRepository playlistRepository;
	
	public long createPlaylist(String playlistName, long userId) {
		Playlist playlist =  Playlist
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
}
