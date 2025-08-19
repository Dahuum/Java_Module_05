package fr.school42.chat.app;

import fr.school42.chat.models.User;
import fr.school42.chat.models.Message;
import fr.school42.chat.models.Chatroom;
import fr.school42.chat.repositories.UsersRepository;
import fr.school42.chat.repositories.UsersRepositoryJdbcImpl;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;

public class Program {
    public static void main(String[] args) {
        try (HikariDataSource dataSource = new HikariDataSource();)  {
            dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/chat_db");
            dataSource.setUsername("postgres");
            dataSource.setPassword("");
            
            UsersRepository repository = new UsersRepositoryJdbcImpl(dataSource);
            
            List<User> users = repository.findAll(0, 3);  // First 3 users
            for (User user : users) {
                System.out.println("👤 User: " + user.getLogin() + " (ID: " + user.getId() + ")");
                
                System.out.print("   🏠 Owns rooms: ");
                if (user.getCreatedRooms().isEmpty()) {
                    System.out.println("(none)");
                } else {
                    for (Chatroom room : user.getCreatedRooms()) {
                        System.out.print(room.getName() + " ");
                    }
                    System.out.println();
                }
                
                System.out.print("   👥 Member of: ");
                if (user.getRooms().isEmpty()) {
                    System.out.println("(none)");
                } else {
                    for (Chatroom room : user.getRooms()) {
                        System.out.print(room.getName() + " ");
                    }
                    System.out.println();
                }
                
                System.out.println(); // Empty line between users
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }
}
