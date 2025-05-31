package com.example.userapi.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

// @Entity annotation marks this class as a JPA entity that maps to a database table
@Entity
// @Table specifies the table name in the database (optional if class name matches table name)
@Table(name = "users")
public class User {
    
    // @Id marks this field as the primary key
    @Id
    // @GeneratedValue specifies how the primary key should be generated
    // GenerationType.SEQUENCE uses Oracle sequences for auto-increment
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    // @SequenceGenerator defines the sequence generator for Oracle
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
    private Long id;
    
    // @Column annotation is optional for basic mappings, but useful for constraints
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(unique = true, nullable = false, length = 150)
    private String email;
    
    @Column(length = 15)
    private String phone;
    
    // Automatically set creation timestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // @PrePersist is called before entity is saved to database
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Default constructor required by JPA
    public User() {}
    
    // Constructor for creating new users
    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters and Setters - Required for JPA and Spring to access fields
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // toString method for debugging and logging
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}