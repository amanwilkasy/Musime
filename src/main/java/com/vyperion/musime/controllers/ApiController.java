package com.vyperion.musime.controllers;

import com.vyperion.musime.dto.FeaturesGraph;
import com.vyperion.musime.dto.Song;
import com.vyperion.musime.services.SpotifyGraphService;
import com.vyperion.musime.services.SpotifyService;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api")
public class ApiController {

    private final SpotifyService spotifyService;

    private final SpotifyGraphService spotifyGraphService;

    public ApiController(SpotifyService spotifyService, SpotifyGraphService spotifyGraphService) {
        this.spotifyService = spotifyService;
        this.spotifyGraphService = spotifyGraphService;
    }

    @GetMapping("getAllPlaylists")
    public ResponseEntity<List<PlaylistSimplified>> getAllPlaylists() {
        return ResponseEntity.ok().body(spotifyService.getAllPlaylists());
    }

    @GetMapping("getTracksFromPlaylist")
    public ResponseEntity<List<PlaylistTrack>> getTracksFromPlaylist() {
        return ResponseEntity.ok().body(spotifyService.getTracksFromPlaylist("37i9dQZF1DWUgX5cUT0GbU"));
    }

    @GetMapping("getAllAudioFeaturesFromPlaylist")
    public ResponseEntity<List<Song>> getAllAudioFeaturesFromPlaylist() {
        return ResponseEntity.ok().body(spotifyService.getAllAudioFeaturesFromPlaylist("37i9dQZF1DWUgX5cUT0GbU"));
    }

    @GetMapping("getAllAudioFeaturesForAllSongs")
    public ResponseEntity<List<Song>> getAllAudioFeaturesForAllSongs() {
        return ResponseEntity.ok().body(spotifyService.getAllAudioFeaturesForAllSongs());
    }

    //xgvpcca56pwz4nykvdkd9ovxs
    @GetMapping("getCurrentUser")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok().body(spotifyService.getCurrentUser());
    }


    //spotifyGraphService

    @GetMapping("getUserGraphFeatures")
    public ResponseEntity<List<FeaturesGraph>> getUserGraphFeatures() {
        return ResponseEntity.ok().body(spotifyGraphService.getUserGraphFeatures());
    }

    @GetMapping("generateUserGraphFeatures")
    public ResponseEntity<List<FeaturesGraph>> generateUserGraphFeatures() {
        return ResponseEntity.ok().body(spotifyGraphService.generateUserGraphFeatures());
    }

    @GetMapping("checkIfGraphExists")
    public ResponseEntity<Boolean> checkIfGraphExists() {
        return ResponseEntity.ok().body(spotifyGraphService.checkIfGraphExists());
    }



}

/*
{
    "birthdate": null,
    "country": "US",
    "displayName": "akasy",
    "email": "amanwilk@gmail.com",
    "externalUrls": {
        "externalUrls": {
            "spotify": "https://open.spotify.com/user/xgvpcca56pwz4nykvdkd9ovxs"
        }
    },
    "followers": {
        "href": null,
        "total": 1
    },
    "href": "https://api.spotify.com/v1/users/xgvpcca56pwz4nykvdkd9ovxs",
    "id": "xgvpcca56pwz4nykvdkd9ovxs",
    "images": [],
    "product": "PREMIUM",
    "type": "USER",
    "uri": "spotify:user:xgvpcca56pwz4nykvdkd9ovxs"
}
 */




