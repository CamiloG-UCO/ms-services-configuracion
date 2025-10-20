package co.edu.hotel.configuracionservice.repository.user;

import co.edu.hotel.configuracionservice.domain.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface IRoleRepository extends JpaRepository<Role, UUID> {


    Optional<Role> findByName(String name);

    Optional<Role> findByNameAndActive(String name, Boolean active);

    List<Role> findByActive(Boolean active);

    boolean existsByName(String name);
}