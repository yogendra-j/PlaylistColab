package com.playlistColab.services;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.playlistColab.dtos.GoogleUser;
import com.playlistColab.dtos.JwtAuthenticationResponse;
import com.playlistColab.entities.User;
import com.playlistColab.exceptions.InternalServerException;
import com.playlistColab.utils.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GoogleService {

    @Autowired private UserService userService;
    @Autowired private JwtUtil tokenProvider;

    @Autowired private RestTemplate restTemplate;

    private final String GOOGLE_API_BASE = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    public JwtAuthenticationResponse loginUser(String accessToken) {
        var googleUser = getGoogleUser(accessToken);

        return userService.findByEmail(googleUser.getEmail())
                .or(() -> Optional.ofNullable(userService.registerUser(convertTo(googleUser))))
                .map(userDetails -> new UsernamePasswordAuthenticationToken(
                        userDetails, null, new ArrayList<>()))
                .map(tokenProvider::generateToken)
                .orElseThrow(() ->
                        new InternalServerException("unable to login google email " + googleUser.getEmail()));
    }

    private User convertTo(GoogleUser googleUser) {
        return User.builder()
                .email(googleUser.getEmail())
                .name(googleUser.getName())
                .password(generatePassword(8))
                .build();
    }

    public GoogleUser getGoogleUser(String accessToken) {
        var path = GOOGLE_API_BASE + accessToken;
        return restTemplate
                .getForObject(path, GoogleUser.class);
    }

    private String generatePassword(int length) {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for(int i = 4; i< length ; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return new String(password);
    }
}
