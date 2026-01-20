package com.wishlist.repository;

import com.wishlist.model.Security;
import com.wishlist.model.enums.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SecurityRepository extends JpaRepository<Security, Long> {
    boolean existsByUsername(String username);

    @Query("SELECT s.role FROM security s WHERE s.id = ?1")
    Role getRoleById(Long id);

    @Query(nativeQuery = true, value = "SELECT * FROM security WHERE role = :roleParam")
    List<Security> findByRole(@Param("roleParam") String roleParam);

    Optional<Security> findByUsername(String username);

    Optional<Security> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE security SET role = :role WHERE id = :securityId")
    int updateRoleById(@Param("securityId") Long securityId,
                       @Param("role") String role);
}

