package com.playlistColab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.playlistColab.services.SpotifyService;

@RestController
public class spotifyHelperController {
    @Autowired SpotifyService spotifyService;
    @GetMapping("/spotify/{playlistId}")
    public ResponseEntity<?> getSongsList(@PathVariable("playlistId") String playlistId, @AuthenticationPrincipal UserDetails userDetails) {
        
        return ResponseEntity.ok(spotifyService.getTracks(playlistId, userDetails.getUsername()));
    }
}
