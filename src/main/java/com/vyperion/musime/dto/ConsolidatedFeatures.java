package com.vyperion.musime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsolidatedFeatures {

    private String feature;
    private List<String> labels;
    private List<Integer> values;

    public ConsolidatedFeatures(String feature, Set<Integer> labels, List<Integer> values) {
        this.feature = feature;
        this.labels = labels.stream().map(String::valueOf).collect(Collectors.toList());
        this.values = values;
    }

}
