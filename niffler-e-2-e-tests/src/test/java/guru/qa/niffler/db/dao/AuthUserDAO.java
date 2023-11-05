package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public interface AuthUserDAO {

    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    int createUser(AuthUserEntity user);

    AuthUserEntity updateUser(AuthUserEntity user);

    void deleteUser(AuthUserEntity userId);

    AuthUserEntity getUserById(UUID userId);
/*var from hw3
    public interface AuthUserDAO {

        PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        int createUser(UserEntity user);

        void deleteUserById(UUID userId);

        UserEntity getUserByUserName(String userName);
    }*/
}
