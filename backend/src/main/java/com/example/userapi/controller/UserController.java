package com.example.userapi.controller;

import com.example.userapi.entity.User;
import com.example.userapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

// @RestController combines @Controller and @ResponseBody
// All methods return data directly (JSON) instead of view names
@RestController
// @RequestMapping sets the base URL path for all endpoints in this controller
@RequestMapping("/api/users")
// @CrossOrigin enables CORS for React frontend running on different port
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    // Inject the UserService to handle business logic
    @Autowired
    private UserService userService;
    
    /**
     * GET /api/users - Retrieve all users
     * HTTP Method: GET
     * Response: JSON array of user objects
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            // Call service method to get all users
            List<User> users = userService.getAllUsers();
            
            // Return HTTP 200 OK with the list of users
            // ResponseEntity allows us to control HTTP status codes and headers
            return new ResponseEntity<>(users, HttpStatus.OK);
            
        } catch (Exception e) {
            // Return HTTP 500 Internal Server Error if something goes wrong
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * GET /api/users/{id} - Retrieve a specific user by ID
     * HTTP Method: GET
     * @PathVariable extracts {id} from the URL path
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            // Get user by ID from service
            Optional<User> user = userService.getUserById(id);
            
            // Check if user exists
            if (user.isPresent()) {
                // Return HTTP 200 OK with user data
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                // Return HTTP 404 Not Found if user doesn't exist
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            
        } catch (Exception e) {
            // Return HTTP 500 Internal Server Error for any other errors
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * POST /api/users - Create a new user
     * HTTP Method: POST
     * @RequestBody converts JSON request body to User object
     * @Valid triggers validation annotations in the User entity
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try {
            // Call service to create new user
            User savedUser = userService.createUser(user);
            
            // Return HTTP 201 Created with the saved user data
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            // Handle business logic errors (like duplicate email)
            // Return HTTP 400 Bad Request with error message
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            // Handle unexpected errors
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * PUT /api/users/{id} - Update an existing user
     * HTTP Method: PUT
     * @PathVariable gets the user ID from URL
     * @RequestBody gets the updated user data from request body
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, 
                                         @Valid @RequestBody User user) {
        try {
            // Call service to update the user
            User updatedUser = userService.updateUser(id, user);
            
            // Return HTTP 200 OK with updated user data
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            // Handle business logic errors (user not found, email conflict)
            if (e.getMessage().contains("not found")) {
                // Return HTTP 404 Not Found if user doesn't exist
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } else {
                // Return HTTP 400 Bad Request for other business logic errors
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
        } catch (Exception e) {
            // Handle unexpected errors
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * DELETE /api/users/{id} - Delete a user
     * HTTP Method: DELETE
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            // Call service to delete user
            boolean deleted = userService.deleteUser(id);
            
            if (deleted) {
                // Return HTTP 200 OK with success message
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            } else {
                // Return HTTP 404 Not Found if user doesn't exist
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            
        } catch (Exception e) {
            // Handle unexpected errors
            return new ResponseEntity<>("Error deleting user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * GET /api/users/search?name={name} - Search users by name
     * HTTP Method: GET with query parameter
     * @RequestParam extracts query parameters from URL
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam(required = false) String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                // If no search term provided, return all users
                return getAllUsers();
            }
            
            // Search users by name using service
            List<User> users = userService.searchUsersByName(name);
            
            // Return HTTP 200 OK with search results
            return new ResponseEntity<>(users, HttpStatus.OK);
            
        } catch (Exception e) {
            // Handle errors
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * GET /api/users/count - Get total user count
     * HTTP Method: GET
     * Returns just a number instead of user objects
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        try {
            // Get user count from service
            long count = userService.getUserCount();
            
            // Return HTTP 200 OK with count
            return new ResponseEntity<>(count, HttpStatus.OK);
            
        } catch (Exception e) {
            // Handle errors
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}