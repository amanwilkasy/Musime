package com.vyperion.musime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeaturesGraph {

    private int id;
    private String userId;
    private String feature;
    private String description;
    private Set<String> labels;
    private Set<Integer> values;

    public FeaturesGraph(String feature,String description, TreeMap<Integer, Integer> consolidated) {
        this.feature = feature;
        this.description = description;
        this.labels = consolidated.keySet().stream().map(String::valueOf).collect(Collectors.toSet());
        this.values = new HashSet<>(new ArrayList<>(consolidated.values()));
    }

}
//    public FeaturesGraph(String feature, Set<Integer> labels, List<Integer> values) {
//        this.feature = feature;
//        this.labels = labels.stream().map(String::valueOf).collect(Collectors.toList());
//        this.values = values;
//    }