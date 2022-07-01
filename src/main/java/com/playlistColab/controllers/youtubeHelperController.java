package com.playlistColab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playlistColab.services.youtubeService;

@RestController
public class youtubeHelperController {
    
    @Autowired youtubeService youtubeService;
    @GetMapping("/youtube/{playlistId}")
    public ResponseEntity<?> getSongsList(@PathVariable("playlistId") String playlistId) {
        
        return ResponseEntity.ok(youtubeService.getSongsList(playlistId));
    }

    @GetMapping("/youtube/spotify")
    public ResponseEntity<?> convertSpotifySongToYoutube(@RequestParam("spotifySongQuery") String spotifySongQuery) {
            
            return ResponseEntity.ok(youtubeService.convertSpotifySongToYoutube(spotifySongQuery));        
    }
}
