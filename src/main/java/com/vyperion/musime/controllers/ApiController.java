package com.vyperion.musime.controllers;

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
import java.util.Map;

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

//    @GetMapping("playlists")
//    public ResponseEntity<List<Paging<PlaylistSimplified>>> getPlaylists() {
//        return ResponseEntity.ok().body(spotifyService.getAllPlaylist());
//    }
//    @GetMapping("tracks/{playlistId}")
//    public ResponseEntity<List<Paging<PlaylistTrack>>> getTracksFromPlaylist(@PathVariable String playlistId) {
//        return ResponseEntity.ok().body(spotifyService.getTracksFromPlaylist(playlistId));
//    }
//
//    @GetMapping("features/{trackId}")
//    public ResponseEntity<AudioFeatures> getFeatures(@PathVariable String trackId) {
//        return ResponseEntity.ok().body(spotifyService.getAudioFeatures(trackId));
//    }
//
//    @PostMapping("features")
//    public ResponseEntity<AudioFeatures[]> getFeaturesForSeveralTracks(@RequestBody Track track) {
//        String[] array = track.getTracks().toArray(new String[0]);
//        return ResponseEntity.ok().body(spotifyService.getAudioFeaturesForSeveralTracks(array));
//    }
//
//    @GetMapping("features")
//    public ResponseEntity<AudioFeatures> getFeatures() {
//        return ResponseEntity.ok().body(spotifyService.getAudioFeatures("2uDTi1PlpSpvAv7IRAoAEU"));
//    }

    final static String playlistID = "37i9dQZF1DWUgX5cUT0GbU";

    @GetMapping("getAllPlaylists")
    public ResponseEntity<List<PlaylistSimplified>> getAllPlaylists() {
        return ResponseEntity.ok().body(spotifyService.getAllPlaylists());
    }

    @GetMapping("getTracksFromPlaylistList")
    public ResponseEntity<List<PlaylistTrack>> getTracksFromPlaylistList() {
        return ResponseEntity.ok().body(spotifyService.getTracksFromPlaylistList(playlistID));
    }

    @GetMapping("getTracksFromPlaylistMap")
    public ResponseEntity<Map<String, PlaylistTrack>> getTracksFromPlaylistMap() {
        return ResponseEntity.ok().body(spotifyService.getTracksFromPlaylistMap(playlistID));
    }

    @GetMapping("getAllTracksFromAllPlaylists")
    public ResponseEntity<Map<String, PlaylistTrack>> getAllTracksFromAllPlaylists() {
        return ResponseEntity.ok().body(spotifyService.getAllTracksFromAllPlaylists());
    }

    @GetMapping("getAllAudioFeaturesFromPlaylist")
    public ResponseEntity<List<Song>> getAllAudioFeaturesFromPlaylist() {
        return ResponseEntity.ok().body(spotifyService.getAllAudioFeaturesFromPlaylist(playlistID));
    }

    @GetMapping("getAllAudioFeaturesForAllSongs")
    public ResponseEntity<List<Song>> getAllAudioFeaturesForAllSongs() {
        return ResponseEntity.ok().body(spotifyService.getAllAudioFeaturesForAllSongs());
    }

    @GetMapping("getAllPlaylistss")
    public ResponseEntity<List<PlaylistSimplified>> getAllPlaylistss() {
        return ResponseEntity.ok().body(spotifyService.getAllPlaylistss());
    }

}




