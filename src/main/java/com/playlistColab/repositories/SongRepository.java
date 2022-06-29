package com.playlistColab.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.playlistColab.entities.Song;

public interface SongRepository extends JpaRepository<Song, String>{
    // List<Song> findSongsByPlaylistsId(long playlistId);
}
