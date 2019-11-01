package com.vyperion.musime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.internal.util.SerializationHelper;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFeatureGraphClient {

    private int id;

    private String userId;

    private List<FeaturesGraph> featuresGraphs;

    private ProcessState.State processState;

    public UserFeatureGraphClient(UserFeatureGraph userFeatureGraph, ProcessState.State processState) {
        this.id = userFeatureGraph.getId();
        this.userId = userFeatureGraph.getUserId();
        this.processState = processState;
        this.featuresGraphs = userFeatureGraph.getFeatureGraph() != null ? (List<FeaturesGraph>) SerializationHelper.deserialize(userFeatureGraph.getFeatureGraph()) : new ArrayList<>();

    }

}















