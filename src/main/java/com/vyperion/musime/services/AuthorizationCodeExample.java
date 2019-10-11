package com.vyperion.musime.services;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class AuthorizationCodeExample {
    private static final String clientId = "19f19dbaf333441b95d99d89515e8af5";
    private static final String clientSecret = "74e2b03c22824ee7801dc9936b8854cf";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://8181bb28.ngrok.io/callback");
    private static final String code = "AQCMX_CzXarPmff0M5O9gEZr1ngp_eO7f43SOOkPyKzbIaxbYLSHQtALME4RWiU5cU8LqTgXdqf-JbDlkEuO7j5mRBCDEm8obWHglbPfGJTAZvBumqPbSS5Gi_uKs39QSyEQu8H1v9JJLg9HP1dxRSq-npVsYPn-kOF_Bs1egkaEFT_rBdyGhShRjy52bWcYh4Qk_b5Bx2Ix6gy9a8AeyXH2y3OWOnhNd4sqr_isLo8-Oy6LPUEy9LBfAHEyutrOLdW14poEwVKF8JEXja6yBOMqti4UbVTX3jEQjdkg";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();
    private static final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
            .build();

    public static AuthorizationCodeCredentials authorizationCode_Sync() {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println(spotifyApi.getAccessToken());
            System.out.println("refresh is -> ".concat(spotifyApi.getRefreshToken()));
            System.out.println(spotifyApi.getTrack("11dFghVXANMlKmJXsNCbNl"));

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());

            return authorizationCodeCredentials;
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public static void authorizationCode_Async() {
        try {
            final CompletableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.join();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }
}
