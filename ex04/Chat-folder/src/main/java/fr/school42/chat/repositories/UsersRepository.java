
package fr.school42.chat.repositories;

import fr.school42.chat.models.User;
import java.util.List;

public interface UsersRepository {
    List<User> findAll(int page, int size);
}