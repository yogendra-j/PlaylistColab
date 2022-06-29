package com.playlistColab.services;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.playlistColab.dtos.JwtAuthenticationResponse;
import com.playlistColab.entities.User;
import com.playlistColab.exceptions.InternalServerException;
import com.playlistColab.utils.JwtUtil;

@Service
public class GoogleService {

    @Autowired private UserService userService;
    @Autowired private JwtUtil tokenProvider;

    @Autowired private RestTemplate restTemplate;

    @Value("${google.api.baseUrl}")
    private String GOOGLE_API_BASE;
    @Value("${google.clientId}")
    private String googleClientId;
    @Value("${google.clientSecret}")
    private String googleClientSecret;

    public JwtAuthenticationResponse loginUser(String idToken) {
        User googleUser = getGoogleUser(idToken);
        googleUser.setPassword(generatePassword(7));
        return userService.findByEmail(googleUser.getEmail())
                .or(() -> Optional.ofNullable(userService.registerUser(googleUser)))
                .map(userDetails -> new UsernamePasswordAuthenticationToken(
                        userDetails.getEmail(), userDetails.getPassword()))
                .map(tokenProvider::generateToken)
                .orElseThrow(() ->
                        new InternalServerException("unable to login google email " + googleUser.getEmail()));
    }

    public User getGoogleUser(String idToken) {
        String access_token_url = GOOGLE_API_BASE;
        access_token_url += "/tokeninfo?id_token=" + idToken;
		ResponseEntity<User> response = restTemplate.getForEntity(access_token_url, User.class);
        return response.getBody();

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
