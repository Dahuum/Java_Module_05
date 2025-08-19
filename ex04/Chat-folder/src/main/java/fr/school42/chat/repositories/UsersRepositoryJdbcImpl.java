package fr.school42.chat.repositories;

import fr.school42.chat.models.User;
import fr.school42.chat.models.Chatroom;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class UsersRepositoryJdbcImpl implements UsersRepository {
    private DataSource  dataSource;
    
    public UsersRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public List<User> findAll(int page, int size) {
        /* Daba al9lawi ana ash ghankteb fhad lkhra */
        
        String sql = """
            WITH user_created_rooms AS (
                SELECT u.id, u.login, u.password, 
                       c.id as created_room_id, c.name as created_room_name
                FROM users u
                LEFT JOIN chatrooms c ON u.id = c.owner_id
            ),
            user_member_rooms AS (
                SELECT u.id, 
                       c.id as member_room_id, c.name as member_room_name
                FROM users u
                LEFT JOIN user_chatrooms uc ON u.id = uc.user_id
                LEFT JOIN chatrooms c ON uc.chatroom_id = c.id
            )
            SELECT DISTINCT ucr.id, ucr.login, ucr.password,
                   ucr.created_room_id, ucr.created_room_name,
                   umr.member_room_id, umr.member_room_name
            FROM user_created_rooms ucr
            FULL OUTER JOIN user_member_rooms umr ON ucr.id = umr.id
            ORDER BY ucr.id
            """;

        
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            // statement.setInt(1, size);
            // statement.setInt(2, offset);
            
            ResultSet rs = statement.executeQuery();
            Map<Long, User> userMap = new LinkedHashMap<>();
            
            while (rs.next()) {
                Long userId = rs.getLong("id");
                String login = rs.getString("login");
                String password = rs.getString("password");
                
                User user = userMap.get(userId);
                if (user == null) {
                    user = new User(userId, login, password, new ArrayList<>(), new ArrayList<>());
                    userMap.put(userId, user);
                }
                
                Long ownedId = rs.getLong("created_room_id");
                if (ownedId != 0) {
                    String ownedName = rs.getString("created_room_name");
                    Chatroom ownedRoom = new Chatroom(ownedId, ownedName, null, null);
                    if (!user.getCreatedRooms().contains(ownedRoom)) user.getCreatedRooms().add(ownedRoom);
                }
                
                Long memberId = rs.getLong("member_room_id");
                if (memberId != 0) {
                    String memberName = rs.getString("member_room_name");
                    Chatroom memberRoom = new Chatroom(memberId, memberName, null, null);
                    if (!user.getRooms().contains(memberRoom)) user.getRooms().add(memberRoom);
                }
            }
            
            List<User> allUsers = new ArrayList<>(userMap.values());
            int start = size * page;
            int end = Math.min(start + size, allUsers.size());
            
            if (start >= allUsers.size()) return new ArrayList<>();            
            return allUsers.subList(start, end);

            
        } catch (SQLException e) { throw new RuntimeException("findALL failed", e); }
    }
}