package com.vyperion.musime.services;

import com.vyperion.musime.dto.FeaturesGraph;
import com.vyperion.musime.repositories.FeaturesGraphRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeaturesGraphService {

    private final FeaturesGraphRepository featuresGraphRepository;

    public FeaturesGraphService(FeaturesGraphRepository featuresGraphRepository) {
        this.featuresGraphRepository = featuresGraphRepository;
    }

    public List<FeaturesGraph> getFeaturesGraphsByUserId(String id){
        return featuresGraphRepository.getAllByUserId(id);
    }

    public void saveFeaturesGraph(FeaturesGraph featuresGraph){
         featuresGraphRepository.saveAndFlush(featuresGraph);
    }

    public void saveFeaturesGraph(List<FeaturesGraph> featuresGraph){
        featuresGraphRepository.saveAll(featuresGraph);
    }
}
