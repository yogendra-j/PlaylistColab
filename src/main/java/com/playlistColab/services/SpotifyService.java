package com.playlistColab.services;


import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.playlistColab.dtos.SpotifyTokenDto;
import com.playlistColab.entities.User;

@Service
public class SpotifyService {
    @Value("${spotify.clientId}")
    private String spotifyClientId;
    @Value("${spotify.clientSecret}")
    private String spotifyClientSecret;
    @Value("${spotify.baseUrl}")
    private String spotifyApiBase;
    @Autowired
    private UserService userService;

    @Autowired private RestTemplate restTemplate;

    //get access token and refresh token from code
    public SpotifyTokenDto getAccessToken(String code, String username) {
        String access_token_url = spotifyApiBase + "/api/token";
        String body = "grant_type=authorization_code&code=" + code + "&redirect_uri=http://localhost:4200/login/spotify";
        String auth = spotifyClientId + ":" + spotifyClientSecret;
        byte[] authEncoded = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(authEncoded);     
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<SpotifyTokenDto> response = restTemplate.postForEntity(access_token_url, request, SpotifyTokenDto.class);
        userService.saveSpotifyToken(response.getBody(), username);
        return response.getBody();
    }

    //get spotify token from db by username
    public SpotifyTokenDto getAccessToken(String username) {
        User user =  userService.findByUsername(username).orElseThrow(() -> {
            throw new IllegalArgumentException("user not found");
        });
        return SpotifyTokenDto.builder()
                .accessToken(user.getSpotifyAccessToken())
                .expiresIn(user.getSpotifyAccessTokenExpiration())
                .build();
    }
}
