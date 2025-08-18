package fr.school42.chat.app;

import fr.school42.chat.repositories.MessagesRepository;
import fr.school42.chat.repositories.MessagesRepositoryJdbcImpl;
import com.zaxxer.hikari.HikariDataSource;
import fr.school42.chat.models.Message;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        try {
            // Create DataSource (connection to database)
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/chat_db");
            dataSource.setUsername("postgres");  // Change this
            dataSource.setPassword("");  // Change this
            
			MessagesRepository repository = new MessagesRepositoryJdbcImpl(dataSource);
            
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a message ID");
            System.out.print("-> ");
            Long id = scanner.nextLong();
            
            Optional<Message> message = repository.findById(id);
            
            if (message.isPresent()) {
                System.out.println("Message found: " + message.get());
                return;
            } else {
                System.out.println("Message not found");
                return;
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }
}
