package com.vyperion.musime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeaturesGraph {

    private String feature;
    private List<String> labels;
    private List<Integer> values;

    public FeaturesGraph(String feature, Set<Integer> labels, List<Integer> values) {
        this.feature = feature;
        this.labels = labels.stream().map(String::valueOf).collect(Collectors.toList());
        this.values = values;
    }

    public FeaturesGraph(String feature, TreeMap<Integer, Integer> consolidated) {
        this.feature = feature;
        this.labels = consolidated.keySet().stream().map(String::valueOf).collect(Collectors.toList());
        this.values = new ArrayList<>(consolidated.values());
    }

}