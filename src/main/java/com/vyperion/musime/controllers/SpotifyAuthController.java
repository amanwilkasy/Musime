package com.vyperion.musime.controllers;

import com.vyperion.musime.services.SpotifyAuthorization;
import com.vyperion.musime.services.SpotifyUtility;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("spotify-auth")
public class SpotifyAuthController {

    private SpotifyAuthorization spotifyAuthorization;

    private final SpotifyUtility spotifyUtility;

    public SpotifyAuthController(SpotifyAuthorization spotifyAuthorization, SpotifyUtility spotifyUtility) {
        this.spotifyAuthorization = spotifyAuthorization;
        this.spotifyUtility = spotifyUtility;
    }

    @GetMapping("login")
    public ResponseEntity<URI> getLogin() {
        return ResponseEntity.ok().body(spotifyAuthorization.authorizeLogin());
    }

    @GetMapping("callback")
    public ResponseEntity<AuthorizationCodeCredentials> callback(@RequestParam("code") String code) {
        spotifyAuthorization.setCode(code);
        AuthorizationCodeCredentials some = spotifyAuthorization.getAccessCredentials();
        System.out.println("Token: ".concat(some.getAccessToken()));
        return ResponseEntity.ok().body(some);
    }

    @GetMapping("refresh")
    public ResponseEntity<AuthorizationCodeCredentials> tempRefresh() {
        return ResponseEntity.ok().body(spotifyAuthorization.setRefresh());
    }


}














