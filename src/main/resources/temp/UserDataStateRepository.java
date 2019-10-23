package temp;

import com.vyperion.musime.dto.UserDataState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataStateRepository extends JpaRepository<UserDataState, String> {
}
