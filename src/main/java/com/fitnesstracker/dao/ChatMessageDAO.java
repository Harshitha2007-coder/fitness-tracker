package com.fitnesstracker.dao;

import com.fitnesstracker.model.ChatMessage;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ChatMessage operations.
 */
public class ChatMessageDAO {

    /**
     * Save a chat message.
     */
    public ChatMessage create(ChatMessage chatMessage) throws SQLException {
        String sql = "INSERT INTO chat_history (user_id, message, response) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, chatMessage.getUserId());
            stmt.setString(2, chatMessage.getMessage());
            stmt.setString(3, chatMessage.getResponse());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    chatMessage.setId(generatedKeys.getInt(1));
                }
            }
        }
        return chatMessage;
    }

    /**
     * Get chat history for a user.
     */
    public List<ChatMessage> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM chat_history WHERE user_id = ? ORDER BY created_at DESC";
        List<ChatMessage> messages = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToChatMessage(rs));
                }
            }
        }
        return messages;
    }

    /**
     * Get recent chat history for a user (limited).
     */
    public List<ChatMessage> findRecentByUserId(int userId, int limit) throws SQLException {
        String sql = "SELECT * FROM chat_history WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        List<ChatMessage> messages = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToChatMessage(rs));
                }
            }
        }
        return messages;
    }

    /**
     * Delete chat history for a user.
     */
    public void deleteByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM chat_history WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to ChatMessage object.
     */
    private ChatMessage mapResultSetToChatMessage(ResultSet rs) throws SQLException {
        ChatMessage message = new ChatMessage();
        message.setId(rs.getInt("id"));
        message.setUserId(rs.getInt("user_id"));
        message.setMessage(rs.getString("message"));
        message.setResponse(rs.getString("response"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            message.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return message;
    }
}
