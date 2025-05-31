package com.example.userapi.service;

import com.example.userapi.entity.User;
import com.example.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// @Service annotation marks this class as a service layer component
// Spring will create a singleton instance and manage its lifecycle
@Service
// @Transactional ensures all methods run within database transactions
// If any operation fails, the entire transaction will be rolled back
@Transactional
public class UserService {
    
    // @Autowired injects the UserRepository dependency
    // Spring will automatically provide the repository implementation
    @Autowired
    private UserRepository userRepository;
    
    // Alternative constructor-based dependency injection (recommended approach)
    // public UserService(UserRepository userRepository) {
    //     this.userRepository = userRepository;
    // }
    
    /**
     * Retrieve all users from the database
     * @return List of all users
     */
    @Transactional(readOnly = true) // Optimize read-only transactions
    public List<User> getAllUsers() {
        // Calls the built-in findAll() method from JpaRepository
        // Executes: SELECT * FROM users
        return userRepository.findAll();
    }
    
    /**
     * Find a user by their ID
     * @param id The user's unique identifier
     * @return Optional containing the user if found, empty Optional otherwise
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        // findById returns Optional<User> to handle cases where user doesn't exist
        // Executes: SELECT * FROM users WHERE id = ?
        return userRepository.findById(id);
    }
    
    /**
     * Find a user by their email address
     * @param email The user's email address
     * @return Optional containing the user if found, empty Optional otherwise
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        // Uses our custom repository method
        // Executes: SELECT * FROM users WHERE email = ?
        return userRepository.findByEmail(email);
    }
    
    /**
     * Create a new user in the database
     * @param user The user object to save
     * @return The saved user with generated ID
     * @throws RuntimeException if email already exists
     */
    public User createUser(User user) {
        // Check if user with this email already exists to prevent duplicates
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        
        // save() method performs INSERT if entity has no ID, UPDATE if ID exists
        // Oracle sequence will generate the ID automatically
        // Executes: INSERT INTO users (name, email, phone, created_at) VALUES (?, ?, ?, ?)
        return userRepository.save(user);
    }
    
    /**
     * Update an existing user
     * @param id The ID of the user to update
     * @param updatedUser The user object with updated data
     * @return The updated user
     * @throws RuntimeException if user not found or email conflict
     */
    public User updateUser(Long id, User updatedUser) {
        // First check if the user exists
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            throw new RuntimeException("User with ID " + id + " not found");
        }
        
        User existingUser = existingUserOpt.get();
        
        // Check for email conflicts (if email is being changed)
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new RuntimeException("Email " + updatedUser.getEmail() + " is already in use");
            }
        }
        
        // Update the fields
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        
        // Save the updated entity
        // Executes: UPDATE users SET name=?, email=?, phone=? WHERE id=?
        return userRepository.save(existingUser);
    }
    
    /**
     * Delete a user by ID
     * @param id The ID of the user to delete
     * @return true if user was deleted, false if user didn't exist
     */
    public boolean deleteUser(Long id) {
        // Check if user exists before attempting to delete
        if (userRepository.existsById(id)) {
            // deleteById() method from JpaRepository
            // Executes: DELETE FROM users WHERE id = ?
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Search users by name (case-insensitive partial match)
     * @param name The name to search for
     * @return List of matching users
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        // Uses our custom repository method
        // Executes: SELECT * FROM users WHERE UPPER(name) LIKE UPPER('%?%')
        return userRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Get the total count of users
     * @return Total number of users in the database
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        // count() method from JpaRepository
        // Executes: SELECT COUNT(*) FROM users
        return userRepository.count();
    }
}