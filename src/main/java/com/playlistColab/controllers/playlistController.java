package com.playlistColab.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.playlistColab.dtos.AddSongDto;
import com.playlistColab.dtos.ApiResponse;
import com.playlistColab.dtos.PlaylistCreateDto;
import com.playlistColab.dtos.PlaylistGetDto;
import com.playlistColab.dtos.SongGetDto;
import com.playlistColab.entities.Playlist;
import com.playlistColab.entities.User;
import com.playlistColab.exceptions.ResourceNotFoundException;
import com.playlistColab.repositories.SongRepository;
import com.playlistColab.services.PlaylistService;
import com.playlistColab.services.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class playlistController {

    @Autowired
    UserService userService;
    @Autowired
    PlaylistService playlistService;
    @Autowired
    SongRepository songRepository;

    @PostMapping(value = "/myplaylists", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPlaylist(@Valid @RequestBody PlaylistCreateDto payload,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("creating playlist {}", payload.getName());

        long userId = userService.findByUsername(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with username " + userDetails.getUsername() + " not found."));

        long playlistId = playlistService.createPlaylist(payload.getName(), userId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/myplaylists/{playlistId}")
                .buildAndExpand(playlistId).toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, "Playlist created successfully"));
    }

    @GetMapping(value = "/myplaylists", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllMyPlaylists(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("retrieving all myplaylists");
        long userId = userService.findByUsername(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with username " + userDetails.getUsername() + " not found."));
        List<PlaylistGetDto> result = playlistService.findByUserId(userId).stream()
                .map(p -> PlaylistGetDto.builder().id(p.getId()).name(p.getName()).build())
                .collect(Collectors.toList());
        return ResponseEntity
                .ok(result);
    }

    @GetMapping(value = "/playlists/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPlaylistById(@PathVariable long id) {
        log.info("retrieving playlist with id {}", id);
        Playlist playlist = playlistService.findById(id);
        PlaylistGetDto result = PlaylistGetDto.fromPlaylist(playlist);
        result.setSongs(SongGetDto.fromSongs(playlist.getSongs()));
        return ResponseEntity
                .ok(result);
    }

    @PostMapping(value = "/playlists/{id}/songs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSongsToPlaylist(@PathVariable long id, @Valid @RequestBody AddSongDto songsList) {
        log.info("saving songs in playlist with id {}", id);
        log.info("saving songs {}", songsList);
        Playlist playlist = playlistService.addSongToPlaylist(id, songsList);
        PlaylistGetDto result = PlaylistGetDto.fromPlaylist(playlist);
        result.setSongs(playlist.getSongs().stream()
                .map(SongGetDto::fromSong)
                .collect(Collectors.toList()));
        return ResponseEntity
                .ok(result);
    }
}
