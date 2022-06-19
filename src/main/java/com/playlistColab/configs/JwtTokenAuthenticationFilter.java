package com.playlistColab.configs;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.playlistColab.services.CustomUserDetailsService;
import com.playlistColab.utils.JwtUtil;

import io.jsonwebtoken.Claims;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtConfig;
    private CustomUserDetailsService userService;

    public JwtTokenAuthenticationFilter(
    		JwtUtil jwtConfig,
    		CustomUserDetailsService userService) {

        this.jwtConfig = jwtConfig;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. get the authentication header. Tokens are supposed to be passed in the authentication header
        String header = request.getHeader("Authorization");

        // 2. validate the header and check the prefix
        if(header == null || !header.startsWith("Bearer")) {
            chain.doFilter(request, response);  		// If not valid, go to the next filter.
            return;
        }

        // If there is no token provided and hence the user won't be authenticated.
        // It's Ok. Maybe the user accessing a public path or asking for a token.

        // All secured paths that needs a token are already defined and secured in config class.
        // And If user tried to access without access token, then he won't be authenticated and an exception will be thrown.

        // 3. Get the token
        String token = header.replace("Bearer", "");
        String email = jwtConfig.getUsernameFromToken(token);
        UserDetails userDetails = userService.loadUserByUsername(email);

        if(jwtConfig.validateToken(token, userDetails)) {

        	UsernamePasswordAuthenticationToken authToken = 
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            SecurityContextHolder.clearContext();
        }

        // go to the next filter in the filter chain
        chain.doFilter(request, response);
    }

}
