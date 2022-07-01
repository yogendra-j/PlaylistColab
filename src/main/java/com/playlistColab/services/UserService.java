package com.playlistColab.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.playlistColab.dtos.JwtAuthenticationResponse;
import com.playlistColab.dtos.OAuthTokenDto;
import com.playlistColab.entities.User;
import com.playlistColab.exceptions.ItemExistsException;
import com.playlistColab.exceptions.ResourceNotFoundException;
import com.playlistColab.repositories.UserRepository;
import com.playlistColab.utils.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil tokenProvider;

    public JwtAuthenticationResponse loginUser(String username, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return tokenProvider.generateToken(authentication);
    }

    public User registerUser(User user) {
        log.info("registering user {}", user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("username {} already exists.", user.getEmail());

            throw new ItemExistsException(
                    String.format("username %s already exists", user.getEmail()));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("email {} already exists.", user.getEmail());

            throw new ItemExistsException(
                    String.format("email %s already exists", user.getEmail()));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public List<User> findAll() {
        log.info("retrieving all users");
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        log.info("retrieving user {}", username);
        return userRepository.findByEmail(username);
    }

    public Optional<User> findByEmail(String username) {
        log.info("retrieving user {}", username);
        return userRepository.findByEmail(username);
    }

    public Optional<User> findById(long id) {
        log.info("retrieving user {}", id);
        return userRepository.findById(id);
    }

    public void saveSpotifyToken(OAuthTokenDto tokenInfo, String userName) {
        User user = findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User with username" + userName + "not found"));
        user.setSpotifyAccessToken(tokenInfo.getAccessToken());
        user.setSpotifyAccessTokenExpiration(System.currentTimeMillis() + tokenInfo.getExpiresIn() * 1000);
        user.setSpotifyRefreshToken(tokenInfo.getRefreshToken());
        userRepository.save(user);
    }

    public void saveSpotifyRefreshedToken(OAuthTokenDto tokenInfo, String userName) {
        User user = findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User with username" + userName + "not found"));
        user.setSpotifyAccessToken(tokenInfo.getAccessToken());
        user.setSpotifyAccessTokenExpiration(System.currentTimeMillis() + tokenInfo.getExpiresIn() * 1000);
        userRepository.save(user);
    }

    public void saveGoogleToken(OAuthTokenDto tokenInfo, String userName) {
        User user = findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User with username" + userName + "not found"));
        user.setGoogleAccessToken(tokenInfo.getAccessToken());
        user.setGoogleAccessTokenExpiration(System.currentTimeMillis() + tokenInfo.getExpiresIn() * 1000);
        user.setGoogleRefreshToken(tokenInfo.getRefreshToken());
        userRepository.save(user);
    }

    public void saveGoogleRefreshedToken(OAuthTokenDto tokenInfo, String userName) {
        User user = findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User with username" + userName + "not found"));
        user.setGoogleAccessToken(tokenInfo.getAccessToken());
        user.setGoogleAccessTokenExpiration(System.currentTimeMillis() + tokenInfo.getExpiresIn() * 1000);
        userRepository.save(user);
    }
}
