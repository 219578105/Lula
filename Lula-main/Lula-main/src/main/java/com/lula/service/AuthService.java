package com.lula.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.lula.dto.AuthResponse;
import com.lula.dto.GoogleAuthRequest;
import com.lula.entity.User;
import com.lula.enums.Role;
import com.lula.repository.UserRepository;
import com.lula.security.JwtService;
import com.lula.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${lula.google.client-id}")
    private String googleClientId;

    @Transactional
    public AuthResponse authenticateWithGoogle(GoogleAuthRequest request) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(request.getIdToken());
        if (idToken == null) {
            throw new RuntimeException("Invalid Google ID token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");
        String googleId = payload.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Role role = Role.CUSTOMER;
                    if (request.getRole() != null && request.getRole().equalsIgnoreCase("provider")) {
                        role = Role.PROVIDER;
                    }
                    return User.builder()
                            .email(email)
                            .name(name)
                            .picture(picture)
                            .googleId(googleId)
                            .role(role)
                            .isActive(true)
                            .build();
                });

        if (user.getId() != null) {
            user.setName(name);
            user.setPicture(picture);
        }

        user = userRepository.save(user);

        UserDetails userDetails = new UserDetailsImpl(user);
        String token = jwtService.generateToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        boolean setupComplete = user.getBusiness() != null || user.getRole() == Role.CUSTOMER;

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .name(user.getName())
                .email(user.getEmail())
                .picture(user.getPicture())
                .role(user.getRole().name())
                .setupComplete(setupComplete)
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        var token = refreshTokenService.verifyRefreshToken(refreshToken);
        User user = token.getUser();
        UserDetails userDetails = new UserDetailsImpl(user);
        String newToken = jwtService.generateToken(userDetails);
        String newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .name(user.getName())
                .email(user.getEmail())
                .picture(user.getPicture())
                .role(user.getRole().name())
                .setupComplete(user.getBusiness() != null || user.getRole() == Role.CUSTOMER)
                .build();
    }
}
