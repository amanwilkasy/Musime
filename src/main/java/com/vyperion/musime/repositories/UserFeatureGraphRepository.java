package com.vyperion.musime.repositories;

import com.vyperion.musime.dto.UserFeatureGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFeatureGraphRepository extends JpaRepository<UserFeatureGraph, Integer> {
    UserFeatureGraph getUserFeatureGraphByUserId(String id);
    boolean existsByUserId(String id);
}
