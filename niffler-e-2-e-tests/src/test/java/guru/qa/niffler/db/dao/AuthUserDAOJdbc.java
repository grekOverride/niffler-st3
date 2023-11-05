package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthUserDAOJdbc implements AuthUserDAO, UserDataUserDAO {

    private static DataSource authDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH);
    private static DataSource userdataDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA);

    @Override
    public int createUser(UserEntity user) {
        int createdRows = 0;
        try (Connection conn = authDs.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

                 PreparedStatement authorityPs = conn.prepareStatement(
                         "INSERT INTO authorities (user_id, authority) " +
                                 "VALUES (?, ?)")) {

                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, pe.encode(user.getPassword()));
                usersPs.setBoolean(3, user.getEnabled());
                usersPs.setBoolean(4, user.getAccountNonExpired());
                usersPs.setBoolean(5, user.getAccountNonLocked());
                usersPs.setBoolean(6, user.getCredentialsNonExpired());

                createdRows = usersPs.executeUpdate();
                UUID generatedUserId;

                try (ResultSet generatedKeys = usersPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedUserId = UUID.fromString(generatedKeys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t obtain id from given ResultSet");
                    }
                }

                for (Authority authority : Authority.values()) {
                    authorityPs.setObject(1, generatedUserId);
                    authorityPs.setString(2, authority.name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                }

                authorityPs.executeBatch();
                user.setId(generatedUserId);
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return createdRows;
    }

    @Override
    public void deleteUserById(UUID userId) {
        try (Connection conn = authDs.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement authorityPs = conn.prepareStatement(
                    "DELETE from authorities WHERE user_id = ?");

                 PreparedStatement usersPs = conn.prepareStatement(
                         "DELETE from users WHERE id = ?")
            ) {
                authorityPs.setObject(1, userId);
                usersPs.setObject(1, userId);

                authorityPs.executeUpdate();
                usersPs.executeUpdate();

                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity getUserByUserName(String userName) {
        UserEntity user;
        try (
                Connection conn = authDs.getConnection();
                PreparedStatement usersPs = conn.prepareStatement(
                        "SELECT * from users WHERE username = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            usersPs.setString(1, userName);

            try (ResultSet rs = usersPs.executeQuery()) {
                if (rs.next()) {
                    user = UserEntity.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .username(rs.getString("username"))
                            .password(pe.encode(rs.getString("password")))
                            .enabled(rs.getBoolean("enabled"))
                            .accountNonExpired(rs.getBoolean("account_non_expired"))
                            .accountNonLocked(rs.getBoolean("account_non_locked"))
                            .credentialsNonExpired(rs.getBoolean("credentials_non_expired"))
                            .build();
                } else {
                    throw new IllegalStateException("Can`t get ResultSet");
                }
            }

            List<AuthorityEntity> authorities = new ArrayList<>();

            try (PreparedStatement authorityPs = conn.prepareStatement(
                    "SELECT * from authorities WHERE user_id = ?")
            ) {
                authorityPs.setObject(1, user.getId());

                try (ResultSet rs = authorityPs.executeQuery()) {
                    while (rs.next()) {

                        authorities.add(
                                AuthorityEntity.builder()
                                        .id(UUID.fromString(rs.getString("id")))
                                        .authority(Authority.valueOf(rs.getString("authority")))
                                        .user(user)
                                        .build()
                        );
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);

            }

            user.setAuthorities(authorities);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public int createUserInUserData(UserEntity user) {
        int createdRows = 0;
        try (
                Connection conn = userdataDs.getConnection();
                PreparedStatement usersPs = conn.prepareStatement(
                        "INSERT INTO users (username, currency) " +
                                "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {

            usersPs.setString(1, user.getUsername());
            usersPs.setString(2, CurrencyValues.RUB.name());

            createdRows = usersPs.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return createdRows;
    }


    @Override
    public UserEntityFromUserData getUserByUserNameInUserData(String userName) {
        UserEntityFromUserData userEntityFromUserData;

        try (
                Connection conn = userdataDs.getConnection();
                PreparedStatement usersPs = conn.prepareStatement(
                        "SELECT * from users WHERE username = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {

            usersPs.setString(1, userName);

            try (ResultSet rs = usersPs.executeQuery()) {
                if (rs.next()) {
                    userEntityFromUserData = UserEntityFromUserData.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .username(rs.getString("username"))
                            .currency(CurrencyValues.valueOf(rs.getString("currency")))
                            .firstname(rs.getString("firstname"))
                            .surname(rs.getString("surname"))
                            .photo(rs.getBytes("photo"))
                            .build();
                } else {
                    throw new IllegalStateException("Can`t get ResultSet");
                }
            }

        } catch (
                SQLException e) {
            throw new RuntimeException(e);
        }
        return userEntityFromUserData;
    }

    @Override
    public void deleteUserByIdInUserData(UUID userId) {
        try (
                Connection conn = userdataDs.getConnection();
                PreparedStatement usersPs = conn.prepareStatement(
                        "DELETE from users WHERE id = ?")
        ) {
            usersPs.setObject(1, userId);
            usersPs.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
