package com.vyperion.musime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.internal.util.SerializationHelper;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFeatureGraphClient {

    private int id;

    private String userId;

    private List<FeaturesGraph> featuresGraphs;

    public UserFeatureGraphClient(UserFeatureGraph userFeatureGraph) {
        this.id = userFeatureGraph.getId();
        this.userId = userFeatureGraph.getUserId();
        this.featuresGraphs = (List<FeaturesGraph>) SerializationHelper.deserialize(userFeatureGraph.getFeatureGraph());
    }
}















