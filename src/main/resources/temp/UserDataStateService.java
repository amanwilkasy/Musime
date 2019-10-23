package temp;

import com.vyperion.musime.dto.UserDataState;
import com.vyperion.musime.repositories.UserDataStateRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserDataStateService {

    private final UserDataStateRepository userDataStateRepository;

    private final SpotifyService spotifyService;


    public UserDataStateService(SpotifyService spotifyService, UserDataStateRepository userDataStateRepository) {
        this.userDataStateRepository = userDataStateRepository;
        this.spotifyService = spotifyService;
    }

    public UserDataState getUserDataState(){
        Optional<UserDataState> optional =  userDataStateRepository.findById(spotifyService.getCurrentUser().getId());

        if (optional.isPresent()){
            return optional.get();
        }else{
            String path  = UserDataState.songDirectory.concat(spotifyService.getCurrentUser().getId()).concat(".json");
            saveUserDataState(new UserDataState(spotifyService.getCurrentUser().getId(), UserDataState.State.NONE, path));
            return getUserDataState();
        }
    }

    public void saveUserDataState(UserDataState userDataState){
        userDataStateRepository.save(userDataState);
    }

    public void updateState(UserDataState.State state){
        UserDataState userDataState = getUserDataState();
        userDataState.setState(state);
        userDataStateRepository.save(userDataState);

    }
}
