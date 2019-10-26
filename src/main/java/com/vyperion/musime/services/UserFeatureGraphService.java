package com.vyperion.musime.services;

import com.vyperion.musime.dto.UserFeatureGraph;
import com.vyperion.musime.repositories.UserFeatureGraphRepository;
import org.springframework.stereotype.Service;

@Service
public class UserFeatureGraphService {

    private final UserFeatureGraphRepository userFeatureGraphRepository;

    public UserFeatureGraphService(UserFeatureGraphRepository userFeatureGraphRepository) {
        this.userFeatureGraphRepository = userFeatureGraphRepository;
    }


    public UserFeatureGraph getFeaturesGraphsByUserId(String id) {
        return userFeatureGraphRepository.getUserFeatureGraphByUserId(id);
    }

    public boolean userFeatureGraphExists(String id){
        return userFeatureGraphRepository.existsByUserId(id);
    }

    public void saveFeaturesGraph(UserFeatureGraph userFeatureGraph) {
        if (!userFeatureGraphExists(userFeatureGraph.getUserId())) {
            userFeatureGraphRepository.save(userFeatureGraph);
        } else {
            UserFeatureGraph exists = getFeaturesGraphsByUserId(userFeatureGraph.getUserId());
            exists.setFeatureGraph(userFeatureGraph.getFeatureGraph());
            userFeatureGraphRepository.save(exists);
        }

    }

}








