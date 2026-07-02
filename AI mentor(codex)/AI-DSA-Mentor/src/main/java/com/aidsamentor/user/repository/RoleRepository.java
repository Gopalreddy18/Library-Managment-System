package com.aidsamentor.user.repository;

import com.aidsamentor.user.entity.Role;
import com.aidsamentor.user.entity.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
