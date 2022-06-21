package com.playlistColab.controllers;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.playlistColab.dtos.ApiResponse;
import com.playlistColab.dtos.PlaylistCreateDto;
import com.playlistColab.entities.User;
import com.playlistColab.exceptions.ResourceNotFoundException;
import com.playlistColab.services.PlaylistService;
import com.playlistColab.services.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class playlistController {
	
	@Autowired UserService userService;
	@Autowired PlaylistService playlistService;
	
    @PostMapping(value = "/myplaylists", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPlaylist(@Valid @RequestBody PlaylistCreateDto payload, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("creating playlist {}", payload.getName());

        long userId = userService.findByUsername(userDetails.getUsername())
        		.map(User::getId)
				.orElseThrow(() -> new ResourceNotFoundException("User with username " + userDetails.getUsername() + " not found."));

        long playlistId = playlistService.createPlaylist(payload.getName(), userId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/myplaylists/{playlistId}")
                .buildAndExpand(playlistId).toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true,"Playlist created successfully"));
    }

    @GetMapping(value = "/myplaylists", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllMyPlaylists(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("retrieving all myplaylists");
        long userId = userService.findByUsername(userDetails.getUsername())
        		.map(User::getId)
				.orElseThrow(() -> new ResourceNotFoundException("User with username " + userDetails.getUsername() + " not found."));

        return ResponseEntity
                .ok(playlistService.findByUserId(userId));
    }
}
