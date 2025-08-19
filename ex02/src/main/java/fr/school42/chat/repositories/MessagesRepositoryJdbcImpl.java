package fr.school42.chat.repositories;

import fr.school42.chat.models.Message;
import fr.school42.chat.models.Chatroom;
import fr.school42.chat.models.User;


import java.util.Optional;

import javax.sql.DataSource;

import java.time.LocalDateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessagesRepositoryJdbcImpl implements MessagesRepository {

    private DataSource dataSource;
    
    public MessagesRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Optional<Message> findById(Long id) {
        String sql = "SELECT m.id, m.text, m.datetime, u.id as user_id, u.login, u.password, r.id as room_id, r.name as room_name " +
            "FROM messages m " +
            "JOIN users u ON m.author_id = u.id " +
            "JOIN chatrooms r ON m.room_id = r.id " +
            "WHERE m.id = ?";
        
            try (Connection connection = dataSource.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {
                    
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("resultSet: " + resultSet);
            
            if (resultSet.next()) {
                /* Read message data */
                Long messageId = resultSet.getLong("id");
                String messageText = resultSet.getString("text");
                
                /* Read author data */
                Long userId = resultSet.getLong("user_id");
                String userLogin = resultSet.getString("login");
                String password = resultSet.getString("password");
                
                /* Read room data */  
                Long roomId = resultSet.getLong("room_id");
                String roomName = resultSet.getString("room_name");
                
                /* Create objects */
                User author = new User(userId, userLogin, password, null, null);
                Chatroom room = new Chatroom(roomId, roomName, null, null);
                Message message = new Message(messageId, author, room, messageText, null);
                
                return Optional.of(message);
            }
            return Optional.empty();
                
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    
    @Override
    public void save(Message message) {
        
        if (message.getAuthor() == null || message.getAuthor().getId() == null) throw new NotSavedSubEntityException("Message author cannot be null or have null ID");
        
        if (message.getRoom() == null || message.getRoom().getId() == null) throw new NotSavedSubEntityException("Message room cannot be null or have null ID");
        
        if (!userExists(message.getAuthor().getId())) throw new NotSavedSubEntityException("Author with ID " + message.getAuthor().getId() + " does not exist");
        
        if (!roomExists(message.getRoom().getId())) throw new NotSavedSubEntityException("Room with ID " + message.getRoom().getId() + " does not exist");
        
        String sql = "INSERT INTO messages  (author_id, room_id, text, datetime) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setLong(1, message.getAuthor().getId());
            statement.setLong(2, message.getRoom().getId());
            statement.setString(3, message.getText());
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(message.getDateTime()));
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long generatedId = resultSet.getLong("id");
                message.setId(generatedId);
            }
        } catch (Exception e) { throw new RuntimeException("Failed to save message", e); }
    }
    
    private boolean userExists(Long userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;  // Count > 0 means user exists
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if user exists", e);
        }
        
        return false;
    }
    
    private boolean roomExists(Long roomId) {
        String sql = "SELECT COUNT(*) FROM chatrooms WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, roomId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;  // Count > 0 means room exists
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if room exists", e);
        }
        
        return false;
    }

}