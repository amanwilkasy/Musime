package com.vyperion.musime.controllers;

import com.vyperion.musime.dto.FeaturesGraph;
import com.vyperion.musime.dto.Song;
import com.vyperion.musime.services.SpotifyService;
import com.vyperion.musime.services.SpotifyGraphService;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

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

    @GetMapping("getCurrentUser")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok().body(spotifyService.getCurrentUser());
    }


    @GetMapping("getConsolidatedFeatures")
    public ResponseEntity<List<FeaturesGraph>> getConsolidatedFeatures() throws InterruptedException, ExecutionException {

        return null;
    }

}




