package com.vyperion.musime.services;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Service
public class SpotifyAuthorization {
    private String code;
    private static final String clientId = "19f19dbaf333441b95d99d89515e8af5";
    private static final String clientSecret = "74e2b03c22824ee7801dc9936b8854cf";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://70c79a96.ngrok.io/spotify-auth/callback");
    private static final String scopes = "user-read-email,playlist-read-private,playlist-read-collaborative,playlist-read-private";
    private SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();


    public URI authorizeLogin() {
        return getAuthorizationCodeUriRequest(scopes)
                .execute();
    }
    public URI authorizeLogin(String scopes) {
        return getAuthorizationCodeUriRequest(scopes)
                .execute();
    }

    private AuthorizationCodeUriRequest getAuthorizationCodeUriRequest(String scopes) {
        //"user-read-email,playlist-read-private,playlist-read-collaborative,playlist-read-private"
        return spotifyApi.authorizationCodeUri()
                .scope(scopes)
                .response_type("code")
                .show_dialog(false)
                .build();
    }

    public ClientCredentials getNoAuthToken() {
        ClientCredentials clientCredentials = null;
        try {
            clientCredentials = getClientCredentialsRequest().execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return clientCredentials;
    }

    private ClientCredentialsRequest getClientCredentialsRequest(){
        return spotifyApi.clientCredentials().build();
    }

    public AuthorizationCodeCredentials getAccessCredentials() {
        AuthorizationCodeCredentials authorizationCodeCredentials = null;
        try {
            authorizationCodeCredentials = getAuthorizationCodeRequest().execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return authorizationCodeCredentials;
    }

    private AuthorizationCodeRequest getAuthorizationCodeRequest(){
        return spotifyApi.authorizationCode(code).build();
    }

    public AuthorizationCodeCredentials setRefresh() {
        AuthorizationCodeCredentials authorizationRefreshCredentials = null;
        try {
            authorizationRefreshCredentials = getAuthorizationCodeRefreshRequest().execute();
            spotifyApi.setAccessToken(authorizationRefreshCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationRefreshCredentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return authorizationRefreshCredentials;
    }

    private AuthorizationCodeRefreshRequest getAuthorizationCodeRefreshRequest() {
        return spotifyApi.authorizationCodeRefresh().build();
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Bean(name = "SpotifyApi")
    SpotifyApi getSpotifyApi(){
        return this.spotifyApi;
    }


}















