package com.vyperion.musime.repositories;

import com.vyperion.musime.dto.ProcessState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessStateRepository extends JpaRepository<ProcessState, Integer> {
    ProcessState getProcessStateByUserId(String id);
    boolean existsByUserId(String id);
}
