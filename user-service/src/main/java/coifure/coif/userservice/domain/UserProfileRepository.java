package coifure.coif.userservice.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, String> {

    boolean existsByEmailIgnoreCase(String email);

    List<UserProfileEntity> findAllByRoleIgnoreCase(String role);

    List<UserProfileEntity> findAllByStatusIgnoreCase(String status);

    List<UserProfileEntity> findAllByRoleIgnoreCaseAndStatusIgnoreCase(String role, String status);
}
