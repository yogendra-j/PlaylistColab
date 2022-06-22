package com.playlistColab.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.playlistColab.dtos.SongGetDto;
import com.playlistColab.dtos.youtubeSongDto;
import com.playlistColab.dtos.youtubeSongsResult;
import com.playlistColab.exceptions.ResourceNotFoundException;

@Service
public class youtubeService {
    @Value("${youtube.api.key}")
    private String youtubeApiKey;
    @Value("${youtube.api.baseUrl}")
    private String youtubeApiBaseUrl;
    @Autowired
    RestTemplate restTemplate;

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
        do  {
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
}
