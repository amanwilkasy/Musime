package com.vyperion.musime.services;

import com.vyperion.musime.dto.UserDataState;
import com.vyperion.musime.repositories.UserDataStateRepository;
import com.wrapper.spotify.model_objects.specification.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class UserDataStateService {

    private final UserDataStateRepository userDataStateRepository;

    private final User user;

    public UserDataStateService(@Qualifier("CurrentUser") User user, UserDataStateRepository userDataStateRepository) {
        this.userDataStateRepository = userDataStateRepository;
        this.user = user;
    }

    public UserDataState getUserDataState(){
        return userDataStateRepository.findById(user.getId()).orElseThrow(RuntimeException::new);
    }

    public void updateState(UserDataState.State state){
        UserDataState userDataState = getUserDataState();
        userDataState.setState(state);
        userDataStateRepository.save(userDataState);

    }
}
