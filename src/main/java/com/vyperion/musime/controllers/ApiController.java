package com.vyperion.musime.controllers;

import com.vyperion.musime.dto.ProcessState;
import com.vyperion.musime.dto.Song;
import com.vyperion.musime.dto.UserFeatureGraph;
import com.vyperion.musime.dto.UserFeatureGraphClient;
import com.vyperion.musime.services.ProcessFeatureGraph;
import com.vyperion.musime.services.ProcessStateService;
import com.vyperion.musime.services.SpotifyService;
import com.vyperion.musime.services.UserFeatureGraphService;
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
    private final UserFeatureGraphService userFeatureGraphService;
    private final ProcessStateService processStateService;
    private final ProcessFeatureGraph processFeatureGraph;

    public ApiController(SpotifyService spotifyService, UserFeatureGraphService userFeatureGraphService, ProcessStateService processStateService, ProcessFeatureGraph processFeatureGraph) {
        this.spotifyService = spotifyService;
        this.userFeatureGraphService = userFeatureGraphService;
        this.processStateService = processStateService;
        this.processFeatureGraph = processFeatureGraph;
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


    @GetMapping("getFeaturesGraphs")
    public ResponseEntity<UserFeatureGraphClient> getFeaturesGraphsByUserId() {
        String id = spotifyService.getCurrentUser().getId();
        ProcessState processState = processStateService.getProcessStateByUserId(id);
        if (processState == null) {
            processState = new ProcessState(id, ProcessState.State.NONE);
        }
        ProcessState.State state = processState.getState();
        ResponseEntity<UserFeatureGraphClient> responseEntity = null;

        log.info("started master get all playlist data");

        if (state.equals(ProcessState.State.NONE)) {
            log.info("hit ProcessState.State.NONE");

            processState.setState(ProcessState.State.PROCESSING);
            processStateService.saveProcessStateByUserId(processState);

            log.info("state now is ProcessState.State.PROCESSING");

            new Thread(processFeatureGraph).start();

            responseEntity = ResponseEntity.ok().body(new UserFeatureGraphClient(new UserFeatureGraph(), processState.getState()));

        } else if (state.equals(ProcessState.State.PROCESSING)) {

            log.info("hit ProcessState.State.PROCESSING");

            responseEntity = ResponseEntity.ok().body(new UserFeatureGraphClient(new UserFeatureGraph(), state));

        } else if (state.equals(ProcessState.State.DONE)) {

            log.info("hit ProcessState.State.DONE");

            responseEntity = ResponseEntity.ok().body(new UserFeatureGraphClient(userFeatureGraphService.getFeaturesGraphsByUserId(id), state));

        }

        return responseEntity;


    }
}








