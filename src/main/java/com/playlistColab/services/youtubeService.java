package com.playlistColab.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.playlistColab.dtos.OAuthTokenDto;
import com.playlistColab.dtos.SongGetDto;
import com.playlistColab.dtos.YoutubeSearchResult;
import com.playlistColab.dtos.youtubeSearchSongDto;
import com.playlistColab.dtos.youtubeSongDto;
import com.playlistColab.dtos.youtubeSongsResult;
import com.playlistColab.entities.User;
import com.playlistColab.exceptions.ResourceNotFoundException;

@Service
public class YoutubeService {
    @Value("${youtube.api.key1}")
    private String youtubeApiKey;
    @Value("${youtube.api.key2}")
    private String youtubeApiKey2;
    @Value("${youtube.api.key3}")
    private String youtubeApiKey3;
    @Value("${youtube.api.key4}")
    private String youtubeApiKey4;
    @Value("${youtube.api.key5}")
    private String youtubeApiKey5;
    @Value("${google.clientId}")
    private String googleClientId;
    @Value("${google.clientSecret}")
    private String googleClientSecret;
    @Value("${youtube.api.baseUrl}")
    private String youtubeApiBaseUrl;
    @Value("${google.api.baseUrl}")
    private String googleApiBaseUrl;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    UserService userService;

    public List<SongGetDto> getSongsList(String playlistId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(youtubeApiBaseUrl + "/playlistItems")
                .queryParam("playlistId", "{playlistId}")
                .queryParam("key", "{key}")
                .queryParam("part", "{part}")
                .queryParam("pageToken", "{pageToken}")
                .queryParam("maxResults", "{maxResults}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("playlistId", playlistId);
        params.put("key", youtubeApiKey);
        params.put("part", "snippet");
        params.put("maxResults", "50");
        params.put("nextPageToken", "");

        List<SongGetDto> allSongs = new ArrayList<SongGetDto>();
        String nextPageToken = "";
        do {
            params.put("pageToken", nextPageToken);
            ResponseEntity<youtubeSongsResult> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    youtubeSongsResult.class,
                    params);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResourceNotFoundException("Playlist with id " + playlistId + " not found.");
            }

            youtubeSongsResult result = response.getBody();
            allSongs.addAll(result.getSongs().stream()
                    .map(youtubeSongDto::toSongGetDto)
                    .collect(Collectors.toList()));
            nextPageToken = result.getNextPageToken();
        } while (nextPageToken != null);

        return allSongs;

    }

    @Async
    public CompletableFuture<SongGetDto> convertSpotifySongToYoutube(String songQuery, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        // String auth = getAccessToken(username).getAccessToken();
        // headers.set("Authorization", "Bearer " + auth);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(youtubeApiBaseUrl + "/search")
                .queryParam("key", "{key}")
                .queryParam("part", "{part}")
                .queryParam("maxResults", "{maxResults}")
                .queryParam("q", "{q}")
                .queryParam("order", "{order}")
                .queryParam("type", "{type}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("key", getRandomYoutubeApiKey());
        params.put("part", "snippet");
        params.put("maxResults", "1");
        params.put("order", "relevance");
        params.put("type", "video");
        params.put("q", songQuery);
        ResponseEntity<YoutubeSearchResult> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                YoutubeSearchResult.class,
                params);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResourceNotFoundException("Cannot get song with query " + songQuery);
        }

        YoutubeSearchResult result = response.getBody();
        return CompletableFuture.completedFuture((result.getSongs().stream()
                .map(youtubeSearchSongDto::toSongGetDto)
                .collect(Collectors.toList())).get(0));
    }

    public OAuthTokenDto getAccessToken(String code, String username) {
        String access_token_url = googleApiBaseUrl + "/token";
        String body = "grant_type=authorization_code&code=" + code
                + "&redirect_uri=http://localhost:4200/login/google";
        String auth = googleClientId + ":" + googleClientSecret;
        byte[] authEncoded = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(authEncoded);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<OAuthTokenDto> response = restTemplate.postForEntity(access_token_url, request,
                OAuthTokenDto.class);
        userService.saveGoogleToken(response.getBody(), username);
        return response.getBody();
    }

    // get google token from db by username
    public OAuthTokenDto getAccessToken(String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> {
            throw new IllegalArgumentException("user not found");
        });
        OAuthTokenDto token = OAuthTokenDto.builder()
                .accessToken(user.getGoogleAccessToken())
                .expiresIn(user.getGoogleAccessTokenExpiration())
                .build();
        // check if valid
        if (token.getExpiresIn() < System.currentTimeMillis()) {
            token = getRefreshedToken(user.getGoogleRefreshToken(), username);
        }
        return token;
    }

    private OAuthTokenDto getRefreshedToken(String GoogleRefreshToken, String username) {
        String refresh_token_url = googleApiBaseUrl + "/token";
        String body = "grant_type=refresh_token&refresh_token=" + GoogleRefreshToken;
        String auth = googleClientId + ":" + googleClientSecret;
        byte[] authEncoded = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(authEncoded);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<OAuthTokenDto> response = restTemplate.postForEntity(refresh_token_url, request,
                OAuthTokenDto.class);
        userService.saveGoogleRefreshedToken(response.getBody(), username);
        return response.getBody();
    }

    public String getRandomYoutubeApiKey() {
        List<String> keyPool = new ArrayList<>(Arrays.asList(youtubeApiKey2, youtubeApiKey3, youtubeApiKey4, youtubeApiKey5));
        int randomIndex = new Random().nextInt(keyPool.size());
        return keyPool.get(randomIndex);
    }
}
