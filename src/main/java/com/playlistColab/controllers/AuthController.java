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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.playlistColab.dtos.ApiResponse;
import com.playlistColab.dtos.JwtAuthenticationResponse;
import com.playlistColab.dtos.LoginRequest;
import com.playlistColab.dtos.SignUpRequest;
import com.playlistColab.dtos.OAuthTokenDto;
import com.playlistColab.entities.User;
import com.playlistColab.services.GoogleService;
import com.playlistColab.services.SpotifyService;
import com.playlistColab.services.UserService;
import com.playlistColab.services.YoutubeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private GoogleService googleService;
    @Autowired private SpotifyService soptifyService;
    @Autowired private YoutubeService youtubeService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    	JwtAuthenticationResponse token = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping(path = "/google/signin")
    public  ResponseEntity<?> googleAuth(@RequestParam String idToken) {
        log.info("google login {}", idToken);
        JwtAuthenticationResponse token = googleService.loginUser(idToken);
        return ResponseEntity.ok(token);
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequest payload) {
        log.info("creating user {}", payload.getEmail());

        User user = User
                .builder()
                .name(payload.getName())
                .email(payload.getEmail())
                .password(payload.getPassword())
                .build();

        userService.registerUser(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(user.getEmail()).toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true,"User registered successfully"));
    }

    @PostMapping(value = "/spotify/accesstoken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccessToken(@RequestParam String code, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("get access token {}", code);
        OAuthTokenDto accessToken = soptifyService.getAccessToken(code, userDetails.getUsername());
        accessToken.setRefreshToken(null);
        accessToken.setExpiresIn(System.currentTimeMillis() + accessToken.getExpiresIn() * 1000);
        return ResponseEntity.ok(accessToken);
    }

    @GetMapping(value = "/spotify/accesstoken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccessToken(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("get access token for user {}", userDetails.getUsername());
        OAuthTokenDto accessToken = soptifyService.getAccessToken(userDetails.getUsername());
        accessToken.setRefreshToken(null);
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping(value = "/youtube/accesstoken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getGoogleAccessToken(@RequestParam String code, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("get access token {}", code);
        OAuthTokenDto accessToken = youtubeService.getAccessToken(code, userDetails.getUsername());
        accessToken.setRefreshToken(null);
        accessToken.setExpiresIn(System.currentTimeMillis() + accessToken.getExpiresIn() * 1000);
        return ResponseEntity.ok(accessToken);
    }

    @GetMapping(value = "/youtube/accesstoken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getGoogleAccessToken(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("get access token for user {}", userDetails.getUsername());
        OAuthTokenDto accessToken = youtubeService.getAccessToken(userDetails.getUsername());
        accessToken.setRefreshToken(null);
        return ResponseEntity.ok(accessToken);
    }
}
