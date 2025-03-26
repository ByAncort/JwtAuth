package com.app.authjwt.User.Repository;

import com.app.authjwt.User.Model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
