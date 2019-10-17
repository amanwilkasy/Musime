package com.vyperion.musime.controllers;

import com.vyperion.musime.dto.Song;
import com.vyperion.musime.services.SpotifyService;
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

    public ApiController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("getAllAudioFeaturesForAllSongs")
    public ResponseEntity<List<Song>> getAllAudioFeaturesForAllSongs() {
        return ResponseEntity.ok().body(spotifyService.getAllAudioFeaturesForAllSongs());
    }

}




