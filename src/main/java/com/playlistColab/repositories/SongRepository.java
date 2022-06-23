package com.playlistColab.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.playlistColab.entities.Songs;

public interface SongRepository extends JpaRepository<Songs, Long>{
    List<Songs> findAllByYoutubeId(Iterable<String> youtubeId);
}
