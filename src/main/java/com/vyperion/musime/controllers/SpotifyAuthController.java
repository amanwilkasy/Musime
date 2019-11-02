package com.vyperion.musime.controllers;

import com.vyperion.musime.services.SpotifyAuthorization;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequestMapping("spotify-auth")
public class SpotifyAuthController {

    private SpotifyAuthorization spotifyAuthorization;

    public SpotifyAuthController(SpotifyAuthorization spotifyAuthorization) {
        this.spotifyAuthorization = spotifyAuthorization;
    }

    @GetMapping("login")
    public ResponseEntity<URI> getLogin() {
        return ResponseEntity.ok().body(spotifyAuthorization.authorizeLogin());
    }

    @GetMapping("callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code) throws URISyntaxException {
        spotifyAuthorization.setCode(code);
        URI client = new URI("https://musime-client.herokuapp.com/graphs/?success=true");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(client);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @GetMapping("refresh")
    public ResponseEntity<AuthorizationCodeCredentials> tempRefresh() {
        return ResponseEntity.ok().body(spotifyAuthorization.setRefresh());
    }


}














