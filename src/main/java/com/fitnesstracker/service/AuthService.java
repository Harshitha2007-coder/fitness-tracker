package com.fitnesstracker.service;

import com.fitnesstracker.dao.UserDAO;
import com.fitnesstracker.model.Role;
import com.fitnesstracker.model.User;
import com.fitnesstracker.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service class for authentication operations.
 */
public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Register a new user.
     */
    public User register(String username, String password, String email, String fullName, Role role) throws SQLException {
        // Check if username already exists
        if (userDAO.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Validate password strength
        validatePassword(password);

        // Hash password and create user
        String passwordHash = PasswordUtil.hashPassword(password);
        User user = new User(username, passwordHash, email, fullName, role);
        
        return userDAO.create(user);
    }

    /**
     * Login with username and password.
     */
    public Optional<User> login(String username, String password) throws SQLException {
        Optional<User> userOpt = userDAO.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Change user password.
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        Optional<User> userOpt = userDAO.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
                validatePassword(newPassword);
                String newPasswordHash = PasswordUtil.hashPassword(newPassword);
                userDAO.updatePassword(userId, newPasswordHash);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Get all trainers.
     */
    public List<User> getAllTrainers() throws SQLException {
        return userDAO.findByRole(Role.TRAINER);
    }

    /**
     * Get all individuals.
     */
    public List<User> getAllIndividuals() throws SQLException {
        return userDAO.findByRole(Role.INDIVIDUAL);
    }

    /**
     * Validate password strength.
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        if (!hasUpper || !hasLower || !hasDigit) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter, one lowercase letter, and one digit");
        }
    }
}
