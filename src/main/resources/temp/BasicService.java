//package com.vyperion.musime.services;
//
//import com.wrapper.spotify.SpotifyApi;
//import com.wrapper.spotify.exceptions.SpotifyWebApiException;
//import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
//import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//public class BasicService {
//    private static final String clientId = "19f19dbaf333441b95d99d89515e8af5";
//    private static final String clientSecret = "74e2b03c22824ee7801dc9936b8854cf";
//
//    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
//            .setClientId(clientId)
//            .setClientSecret(clientSecret)
//            .build();
//
//
//    public ClientCredentials getNoAuthToken() {
//        try {
//            final ClientCredentials clientCredentials = getClientCredentialsRequest().execute();
//            // Set access token for further "spotifyApi" object usage
//            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
//            return clientCredentials;
//        } catch (IOException | SpotifyWebApiException e) {
//            System.out.println("Error: " + e.getMessage());
//            return null;
//        }
//    }
//
//    private ClientCredentialsRequest getClientCredentialsRequest(){
//        return spotifyApi.clientCredentials().build();
//    }
//
//}
