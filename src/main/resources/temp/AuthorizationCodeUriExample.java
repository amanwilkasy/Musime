//package com.vyperion.musime.services;
//
//import com.wrapper.spotify.SpotifyApi;
//import com.wrapper.spotify.SpotifyHttpManager;
//import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
//import org.springframework.stereotype.Service;
//
//import java.net.URI;
//import java.util.concurrent.CancellationException;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionException;
//
//@Service
//public class AuthorizationCodeUriExample {
//    private static final String clientId = "19f19dbaf333441b95d99d89515e8af5";
//    private static final String clientSecret = "74e2b03c22824ee7801dc9936b8854cf";
//    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://8181bb28.ngrok.io/callback");
//
//    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
//            .setClientId(clientId)
//            .setClientSecret(clientSecret)
//            .setRedirectUri(redirectUri)
//            .build();
//    private static final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
////          .state("x4xkmn9pu3j6ukrs8n")
//            .scope("user-read-email,playlist-read-private,playlist-read-collaborative,playlist-read-private")
//            .response_type("code")
//            .show_dialog(true)
//            .build();
//
//    public static URI authorizationCodeUri_Sync() {
//        final URI uri = authorizationCodeUriRequest.execute();
//
//        System.out.println("URI: " + uri.toString());
//        return uri;
//    }
//
//    public static void authorizationCodeUri_Async() {
//        try {
//            final CompletableFuture<URI> uriFuture = authorizationCodeUriRequest.executeAsync();
//
//            // Thread free to do other tasks...
//
//            // Example Only. Never block in production code.
//            final URI uri = uriFuture.join();
//
//            System.out.println("URI: " + uri.toString());
//        } catch (CompletionException e) {
//            System.out.println("Error: " + e.getCause().getMessage());
//        } catch (CancellationException e) {
//            System.out.println("Async operation cancelled.");
//        }
//    }
//}
