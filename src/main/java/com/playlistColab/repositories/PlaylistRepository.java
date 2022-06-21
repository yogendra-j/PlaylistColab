package com.playlistColab.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.playlistColab.entities.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
	List<Playlist> findByUserId(Long userId);
}
