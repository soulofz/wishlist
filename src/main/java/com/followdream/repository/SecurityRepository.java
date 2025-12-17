package com.followdream.repository;

import com.followdream.model.Security;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SecurityRepository extends JpaRepository<Security, Long> {
    boolean existsByUsername(String username);

    @Query(nativeQuery = true, value = "SELECT * FROM security WHERE role=::roleParam")
    List<Security> findByRole(String role);

    Optional<Security> findByUsername(String username);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE security SET role = 'ADMIN' WHERE user_id = ::userId")
    int setAdminRoleByUSerId(Long userId);
}
