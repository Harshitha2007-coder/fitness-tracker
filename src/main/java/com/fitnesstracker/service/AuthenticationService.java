package com.fitnesstracker.service;

import com.fitnesstracker.dao.UserDAO;
import com.fitnesstracker.model.User;
import com.fitnesstracker.model.UserRole;
import com.fitnesstracker.util.PasswordUtils;
import com.fitnesstracker.util.ValidationUtils;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Service for user authentication and registration.
 */
public class AuthenticationService {
    
    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Register a new user.
     */
    public User register(String username, String email, String password, 
                         UserRole role, String firstName, String lastName) throws SQLException {
        
        // Validate inputs
        if (!ValidationUtils.isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username. Must be 3-50 characters, alphanumeric and underscores only.");
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        
        if (!PasswordUtils.isValidPassword(password)) {
            throw new IllegalArgumentException(PasswordUtils.getPasswordRequirements());
        }
        
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }

        // Check if username or email already exists
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }
        
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists.");
        }

        // Create user with hashed password
        String passwordHash = PasswordUtils.hashPassword(password);
        User user = new User(username, email, passwordHash, role, 
                           ValidationUtils.sanitizeInput(firstName.trim()), 
                           ValidationUtils.sanitizeInput(lastName.trim()));
        
        return userDAO.create(user);
    }

    /**
     * Authenticate a user with username and password.
     */
    public Optional<User> login(String username, String password) throws SQLException {
        if (username == null || password == null) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDAO.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Authenticate a user with email and password.
     */
    public Optional<User> loginWithEmail(String email, String password) throws SQLException {
        if (email == null || password == null) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDAO.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Change user password.
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword) 
            throws SQLException {
        
        Optional<User> userOpt = userDAO.findById(userId);
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Verify current password
        if (!PasswordUtils.verifyPassword(currentPassword, user.getPasswordHash())) {
            return false;
        }
        
        // Validate new password
        if (!PasswordUtils.isValidPassword(newPassword)) {
            throw new IllegalArgumentException(PasswordUtils.getPasswordRequirements());
        }
        
        // Update password
        String newPasswordHash = PasswordUtils.hashPassword(newPassword);
        userDAO.updatePassword(userId, newPasswordHash);
        
        return true;
    }

    /**
     * Get user by ID.
     */
    public Optional<User> getUserById(int userId) throws SQLException {
        return userDAO.findById(userId);
    }

    /**
     * Update user profile.
     */
    public void updateProfile(User user) throws SQLException {
        if (user.getEmail() != null && !ValidationUtils.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        
        userDAO.update(user);
    }
}
