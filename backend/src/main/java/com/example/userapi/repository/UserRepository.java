package com.example.userapi.repository;

import com.example.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// @Repository annotation marks this interface as a Spring repository component
// This enables Spring to create a proxy implementation with database operations
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // JpaRepository<User, Long> provides built-in CRUD methods:
    // - save(User user) - INSERT or UPDATE
    // - findById(Long id) - SELECT by ID
    // - findAll() - SELECT all records
    // - deleteById(Long id) - DELETE by ID
    // - count() - COUNT records
    
    // Custom query methods using Spring Data JPA naming conventions
    // Spring automatically generates the SQL based on method names
    
    // Find user by email address (case-sensitive)
    // Translates to: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
    
    // Find users whose name contains the given string (case-insensitive)
    // Translates to: SELECT * FROM users WHERE UPPER(name) LIKE UPPER('%?%')
    List<User> findByNameContainingIgnoreCase(String name);
    
    // Find users by phone number
    // Translates to: SELECT * FROM users WHERE phone = ?
    List<User> findByPhone(String phone);
    
    // Check if user exists by email
    // Translates to: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);
    
    // Custom JPQL query to find users created after a certain date
    // @Query allows writing custom queries in JPQL (Java Persistence Query Language)
    @Query("SELECT u FROM User u WHERE u.createdAt > :date ORDER BY u.createdAt DESC")
    List<User> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);
    
    // Native SQL query example for complex operations
    // nativeQuery = true allows writing raw SQL instead of JPQL
    @Query(value = "SELECT * FROM users WHERE ROWNUM <= :limit ORDER BY created_at DESC", 
           nativeQuery = true)
    List<User> findRecentUsers(@Param("limit") int limit);
    
    // Custom query to search users by multiple criteria
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR UPPER(u.name) LIKE UPPER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR UPPER(u.email) LIKE UPPER(CONCAT('%', :email, '%')))")
    List<User> findUsersByNameAndEmail(@Param("name") String name, @Param("email") String email);
}