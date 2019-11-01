package com.vyperion.musime.services;

import com.vyperion.musime.dto.ProcessState;
import com.vyperion.musime.dto.UserFeatureGraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ProcessFeatureGraph implements Runnable{

    private final SpotifyService spotifyService;
    private final UserFeatureGraphService userFeatureGraphService;
    private final ProcessStateService processStateService;

    public ProcessFeatureGraph(SpotifyService spotifyService, UserFeatureGraphService userFeatureGraphService, ProcessStateService processStateService) {
        this.spotifyService = spotifyService;
        this.userFeatureGraphService = userFeatureGraphService;
        this.processStateService = processStateService;
    }

    public void run() {

        log.info("hit process runner");
        UserFeatureGraph userFeatureGraph = spotifyService.generateFeaturesGraphForAllSongs();
        userFeatureGraphService.saveFeaturesGraph(userFeatureGraph);

        ProcessState processState = processStateService.getProcessStateByUserId(userFeatureGraph.getUserId());
        processState.setState(ProcessState.State.DONE);
        processStateService.saveProcessStateByUserId(processState);

        log.info("finished thread and saved new data");
    }

}
