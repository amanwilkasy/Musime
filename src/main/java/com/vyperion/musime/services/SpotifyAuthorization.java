package com.vyperion.musime.services;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Service
public class SpotifyAuthorization {

    private String code;
    private String clientId = System.getenv().get("CLIENTID");
    private String clientSecret = System.getenv().get("CLIENTSECRET");

    private static final URI redirectUri = SpotifyHttpManager.makeUri("https://musime.herokuapp.com/spotify-auth/callback");

    private static final String scopes = "user-read-private,user-read-email,playlist-read-private,user-top-read";
    private SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();


    public URI authorizeLogin() {
        return getAuthorizationCodeUriRequest()
                .execute();
    }

    //"user-read-email,playlist-read-private,playlist-read-collaborative,playlist-read-private"
    private AuthorizationCodeUriRequest getAuthorizationCodeUriRequest() {
        return spotifyApi.authorizationCodeUri()
                .scope(SpotifyAuthorization.scopes)
                .response_type("code")
                .show_dialog(false)
                .build();
    }

    public AuthorizationCodeCredentials getAccessCredentials() {
        AuthorizationCodeCredentials authorizationCodeCredentials = null;
        try {
            authorizationCodeCredentials = spotifyApi.authorizationCode(code).build().execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return authorizationCodeCredentials;
    }


    public AuthorizationCodeCredentials setRefresh() {
        AuthorizationCodeCredentials authorizationRefreshCredentials = null;
        try {
            authorizationRefreshCredentials = spotifyApi.authorizationCodeRefresh().build().execute();
            spotifyApi.setAccessToken(authorizationRefreshCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationRefreshCredentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return authorizationRefreshCredentials;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Bean(name = "SpotifyApi")
    SpotifyApi getSpotifyApi() {
        return this.spotifyApi;
    }


}















