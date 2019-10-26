package com.vyperion.musime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeaturesGraph implements Serializable {

    private String name;

    private String description;

    private List<Integer> labels;

    private List<Integer> values;

    public FeaturesGraph(String name, String description, TreeMap<Integer, Integer> consolidated) {
        this.name = name;
        this.description = description;
        this.labels = new ArrayList<>(consolidated.keySet());
        this.values = new ArrayList<>(consolidated.values());
    }

}