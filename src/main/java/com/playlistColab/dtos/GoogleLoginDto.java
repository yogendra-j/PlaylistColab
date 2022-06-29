package com.playlistColab.dtos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleLoginDto implements Serializable {

    private String accessCode;
    private String email;
    private String name;
    private String idToken;

}
