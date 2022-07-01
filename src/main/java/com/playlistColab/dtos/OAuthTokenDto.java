package com.playlistColab.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthTokenDto {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonIgnore
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private long expiresIn;
}
