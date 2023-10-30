package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.db.model.UserEntityFromUserData;

import java.util.UUID;

public interface UserDataUserDAO {

    int createUserInUserData(UserEntity user);

    void deleteUserByIdInUserData(UUID userId);

    UserEntityFromUserData getUserByUserNameInUserData(String userName);

}
