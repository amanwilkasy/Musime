package com.vyperion.musime.services;

import com.vyperion.musime.dto.ProcessState;
import com.vyperion.musime.repositories.ProcessStateRepository;
import org.springframework.stereotype.Service;

@Service
public class ProcessStateService {

    private final ProcessStateRepository processStateRepository;

    public ProcessStateService(ProcessStateRepository processStateRepository) {
        this.processStateRepository = processStateRepository;
    }

    public ProcessState getProcessStateByUserId(String id) {
        return processStateRepository.getProcessStateByUserId(id);
    }

    public void saveProcessStateByUserId(ProcessState processState) {

        if (!processStateExists(processState.getUserId())) {
            processStateRepository.save(processState);
        } else {
            ProcessState exists = getProcessStateByUserId(processState.getUserId());
            exists.setState(processState.getState());
            processStateRepository.save(exists);
        }
    }


    public boolean processStateExists(String id){
        return processStateRepository.existsByUserId(id);
    }


}
