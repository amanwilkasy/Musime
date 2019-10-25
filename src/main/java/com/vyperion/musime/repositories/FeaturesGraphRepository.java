package com.vyperion.musime.repositories;

import com.vyperion.musime.dto.FeaturesGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeaturesGraphRepository extends JpaRepository<FeaturesGraph, Integer> {

    List<FeaturesGraph> getAllByUserId(String id);
}
