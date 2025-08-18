package fr.school42.chat.repositories;

import fr.school42.chat.models.Message;
import fr.school42.chat.models.Chatroom;
import fr.school42.chat.models.User;


import java.util.Optional;

import javax.sql.DataSource;
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
    public Optional<Message> findById(Long Id) {
        String sql = "SELECT m.id, m.text, m.datetime, u.id as user_id, u.login, u.password, r.id as room_id, r.name as room_name " +
            "FROM messages m " +
            "JOIN users u ON m.author_id = u.id " +
            "JOIN chatrooms r ON m.room_id = r.id " +
            "WHERE m.id = ?";
        
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, Id);
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
	
}