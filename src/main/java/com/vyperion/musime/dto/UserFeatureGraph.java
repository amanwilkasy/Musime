package com.vyperion.musime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "UserFeatureGraph")
public class UserFeatureGraph {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "userEmail", nullable = false)
    private String userEmail;

    @Column(name = "featureGraph", nullable = false)
    private byte[] featureGraph;

    public UserFeatureGraph(String userId, String userEmail, byte[] featureGraph) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.featureGraph = featureGraph;
    }
}















