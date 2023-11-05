package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.userdata.UserDataUserEntity;

import java.util.UUID;

public interface UserDataUserDAO {

    int createUserInUserData(UserDataUserEntity user);

    void deleteUserByIdInUserData(UUID userId);

    void deleteUserByUsernameInUserData(String username);
}
/* var from hw3

public interface UserDataUserDAO {

    int createUserInUserData(UserEntity user);

    void deleteUserByIdInUserData(UUID userId);

    UserEntityFromUserData getUserByUserNameInUserData(String userName);

}*/
