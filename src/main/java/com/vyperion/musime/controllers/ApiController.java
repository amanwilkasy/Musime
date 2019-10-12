package com.vyperion.musime.controllers;

import com.vyperion.musime.dto.Song;
import com.vyperion.musime.dto.Track;
import com.vyperion.musime.services.SpotifyService;
import com.vyperion.musime.services.SpotifyUtility;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("playlists")
    public ResponseEntity<List<Paging<PlaylistSimplified>>> getPlaylists() {
        return ResponseEntity.ok().body(spotifyService.getAllPlaylist());
    }

    @GetMapping("playlists-pagination")
    public ResponseEntity<Paging<PlaylistSimplified>> getAllPlaylistPagination() {
        return ResponseEntity.ok().body(spotifyService.getAllPlaylistsPagination());
    }


    @GetMapping("tracks/{playlistId}")
    public ResponseEntity<List<Paging<PlaylistTrack>>> getTracksFromPlaylist(@PathVariable String playlistId) {
        return ResponseEntity.ok().body(spotifyService.getTracksFromPlaylist(playlistId));
    }

    @GetMapping("features/{trackId}")
    public ResponseEntity<AudioFeatures> getFeatures(@PathVariable String trackId) {
        return ResponseEntity.ok().body(spotifyService.getAudioFeatures(trackId));
    }

    @PostMapping("features")
    public ResponseEntity<AudioFeatures[]> getFeaturesForSeveralTracks(@RequestBody Track track) {
        String[] array = track.getTracks().toArray(new String[0]);
        return ResponseEntity.ok().body(spotifyService.getAudioFeaturesForSeveralTracks(array));
    }

    @GetMapping("master")
    public ResponseEntity<List<Song>> getAllFeatures() {
        return ResponseEntity.ok().body(spotifyUtility.allPlaylistSongsToFeatures());
    }


}




