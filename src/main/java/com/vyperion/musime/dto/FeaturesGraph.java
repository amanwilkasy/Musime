package com.vyperion.musime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FeaturesGraph")
public class FeaturesGraph {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "feature", nullable = false)
    private String feature;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "labels", nullable = false)
    private Set<Integer> labels;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "values", nullable = false)
    private Set<Integer> values;

    public FeaturesGraph(String userId, String feature,String description, TreeMap<Integer, Integer> consolidated) {
        this.userId = userId;
        this.feature = feature;
        this.description = description;
        this.labels = consolidated.keySet();
//        .keySet().stream().map(String::valueOf).collect(Collectors.toSet());
        this.values = new HashSet<>(consolidated.values());
    }

}
//    public FeaturesGraph(String feature, Set<Integer> labels, List<Integer> values) {
//        this.feature = feature;
//        this.labels = labels.stream().map(String::valueOf).collect(Collectors.toList());
//        this.values = values;
//    }