package com.app.authjwt.User.Repository;

import com.app.authjwt.User.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    boolean existsByName(String roleName);

    Optional<Role> findByName(String role_user);
}
