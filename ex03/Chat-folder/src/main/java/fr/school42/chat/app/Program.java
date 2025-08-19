package fr.school42.chat.app;

import fr.school42.chat.repositories.MessagesRepository;
import fr.school42.chat.repositories.MessagesRepositoryJdbcImpl;
import com.zaxxer.hikari.HikariDataSource;
import fr.school42.chat.models.Message;
import fr.school42.chat.models.Chatroom;
import fr.school42.chat.models.User;
import java.util.ArrayList;
import java.time.LocalDateTime;

import java.util.Scanner;
import java.util.Optional;

public class Program {
    public static void main(String[] args) {
        try {
            // Create DataSource (connection to database)
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/chat_db");
            dataSource.setUsername("postgres");  // Change this
            dataSource.setPassword("");  // Change this
            
			MessagesRepository repository = new MessagesRepositoryJdbcImpl(dataSource);
			
			Optional<Message> messageOpt = repository.findById(11L);
            
			if (messageOpt.isPresent()) {
			Message message = messageOpt.get();
			message.setText("Bye from java, Edit l9lawi");
			message.setDateTime(null);
			repository.update(message);
			System.out.println("Message tupdata");
			}
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }
}
