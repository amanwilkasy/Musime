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
    private static final String code = "AQCNhVdhIgEYVPR-nANid6E1nA85mDnceuyGmdnp7SolCtolqwg85RRlfs-fkKdyD2b5IZAmDgySTJVg7Xfq33NACzVr-PWZT10gubaOOhE3GdS-eTRT6MAydmn9apTMnJ1jpyDbPzqdtgKWOH0AG3ChWe6ojT_KMtRr-Yw3hSyeKmJwmndg3fpYwXlD96fL1NTk0RYa5RP3CEsdqddRzOhzyRGqexaLxv5GDxkwfCCf1m--oLhZ0TQwsk2qVlLMtojf-j95YQN5r8opjj5StSACc2KKRsgwq7epVVs3";

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
