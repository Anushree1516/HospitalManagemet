package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.entity.User;
import com.bloodbank.bloodbank.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByRoleAndIsActive(Role role, Boolean isActive);

    // Find nearby users by location (Haversine formula)
    @Query(value = """
            SELECT * FROM users u
            WHERE u.latitude IS NOT NULL AND u.longitude IS NOT NULL
            AND (6371 * acos(cos(radians(:lat)) * cos(radians(u.latitude))
            * cos(radians(u.longitude) - radians(:lng))
            + sin(radians(:lat)) * sin(radians(u.latitude)))) < :radiusKm
            """, nativeQuery = true)
    List<User> findUsersWithinRadius(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radiusKm") double radiusKm
    );
}
