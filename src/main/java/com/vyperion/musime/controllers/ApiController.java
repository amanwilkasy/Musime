package com.vyperion.musime.controllers;

import com.vyperion.musime.dto.ConsolidatedFeatures;
import com.vyperion.musime.dto.Song;
import com.vyperion.musime.services.SpotifyService;
import com.vyperion.musime.services.SpotifyUtility;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
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

    private final SpotifyUtility spotifyUtility;

    public ApiController(SpotifyService spotifyService, SpotifyUtility spotifyUtility) {
        this.spotifyService = spotifyService;
        this.spotifyUtility = spotifyUtility;
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


    @GetMapping("getConsolidatedFeatures")
    public ResponseEntity<List<ConsolidatedFeatures>> getConsolidatedFeatures() throws InterruptedException, ExecutionException {
        //get all features
        List<Song> allFeatures = spotifyService.getAllAudioFeaturesForAllSongs();

        //write them to a file
        String filePath = SpotifyUtility.writeAllSongFeaturesToFile(allFeatures);

        System.out.println(filePath);
        System.out.println("222");

        //read them from file
        List<Song> fileData = SpotifyUtility.readFileIntoSongs(filePath);

        System.out.println("333");

        //set songs
        spotifyUtility.setSongs(fileData);

        System.out.println("444");

//        return json
        return ResponseEntity.ok().body(spotifyUtility.getConsolidatedFeatures());
    }

}




