package co.edu.hotel.configuracionservice.repository.user;

import co.edu.hotel.configuracionservice.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {


    Optional<User> findByEmail(String email);


    Optional<User> findByEmailAndActive(String email, Boolean active);

    boolean existsByEmail(String email);


    List<User> findByActive(Boolean active);


    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);


    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.active = true")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName AND u.active = true")
    long countByRoleNameAndActive(@Param("roleName") String roleName);
}