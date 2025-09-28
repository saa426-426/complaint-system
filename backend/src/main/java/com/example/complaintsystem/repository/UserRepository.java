// backend/src/main/java/com/example/complaintsystem/repository/UserRepository.java
package com.example.complaintsystem.repository;

import com.example.complaintsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRoleAndDepartment(User.Role role, String department);
    List<User> findByRoleAndHostel(User.Role role, String hostel);
    List<User> findByRole(User.Role role);
    List<User> findByDepartment(String department);
    List<User> findByHostel(String hostel);
}
