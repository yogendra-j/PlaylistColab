package com.playlistColab.services;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.playlistColab.dtos.SongGetDto;
import com.playlistColab.dtos.SpotifyPlaylistDto;
import com.playlistColab.dtos.OAuthTokenDto;
import com.playlistColab.dtos.TracksSpotify;
import com.playlistColab.entities.User;

@Service
public class SpotifyService {
    @Value("${spotify.clientId}")
    private String spotifyClientId;
    @Value("${spotify.clientSecret}")
    private String spotifyClientSecret;
    @Value("${spotify.baseUrl}")
    private String spotifyAuthUrl;
    @Value("${spotify.api.url}")
    private String spotifyApiUrl;
    @Autowired
    private UserService userService;

    @Autowired private RestTemplate restTemplate;

    //get access token and refresh token from code
    public OAuthTokenDto getAccessToken(String code, String username) {
        String access_token_url = spotifyAuthUrl + "/api/token";
        String body = "grant_type=authorization_code&code=" + code + "&redirect_uri=http://localhost:4200/login/spotify";
        String auth = spotifyClientId + ":" + spotifyClientSecret;
        byte[] authEncoded = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(authEncoded);     
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<OAuthTokenDto> response = restTemplate.postForEntity(access_token_url, request, OAuthTokenDto.class);
        userService.saveSpotifyToken(response.getBody(), username);
        return response.getBody();
    }

    //get spotify token from db by username
    public OAuthTokenDto getAccessToken(String username) {
        User user =  userService.findByUsername(username).orElseThrow(() -> {
            throw new IllegalArgumentException("user not found");
        });
        OAuthTokenDto token =  OAuthTokenDto.builder()
                .accessToken(user.getSpotifyAccessToken())
                .expiresIn(user.getSpotifyAccessTokenExpiration())
                .build();
        //check if valid
        if (token.getExpiresIn() < System.currentTimeMillis()) {
            token = getRefreshedToken(user.getSpotifyRefreshToken(), username);
        }
        return token;
    }

    private OAuthTokenDto getRefreshedToken(String spotifyRefreshToken, String username) {
        String refresh_token_url = spotifyAuthUrl + "/api/token";
        String body = "grant_type=refresh_token&refresh_token=" + spotifyRefreshToken;
        String auth = spotifyClientId + ":" + spotifyClientSecret;
        byte[] authEncoded = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(authEncoded);     
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<OAuthTokenDto> response = restTemplate.postForEntity(refresh_token_url, request, OAuthTokenDto.class);
        userService.saveSpotifyRefreshedToken(response.getBody(), username);
        return response.getBody();
    }

    //get tracks from playlist id
    public List<SongGetDto> getTracks(String playlistId, String username) {
        List<TracksSpotify> allTracks = new ArrayList<>();
        String tracks_url = spotifyApiUrl + "/playlists/" + playlistId + "/tracks";
        while (tracks_url != null) {
            String auth = getAccessToken(username).getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + auth);
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<SpotifyPlaylistDto> response = restTemplate.exchange(tracks_url, HttpMethod.GET, request, SpotifyPlaylistDto.class);
            allTracks.addAll(response.getBody().getItems().stream().map(item -> item.getTrack()).collect(Collectors.toList()));
            tracks_url = response.getBody().getNext();
        }
        return allTracks.stream().map(SongGetDto::fromTracksSpotify).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
