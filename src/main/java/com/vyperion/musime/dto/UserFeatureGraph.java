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

        @Column(name = "featureGraph", nullable = false)
        private byte[] featureGraph;
}















