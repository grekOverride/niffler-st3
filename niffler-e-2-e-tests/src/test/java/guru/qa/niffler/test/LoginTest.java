package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.db.model.UserEntityFromUserData;
import guru.qa.niffler.jupiter.DBUser;
import guru.qa.niffler.jupiter.Dao;
import guru.qa.niffler.jupiter.DaoExtension;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ExtendWith(DaoExtension.class)
public class LoginTest extends BaseWebTest {

    private final String userNameForTest = "valentin_1111";

    @Dao
    private AuthUserDAO authUserDAO;
    @Dao
    private UserDataUserDAO userDataUserDAO;

    @BeforeEach
    @DBUser(
            username = userNameForTest,
            password = "12345",
            enabled = true,
            accountNonExpired = true,
            accountNonLocked = true,
            credentialsNonExpired = true,
            authorities = {Authority.read,Authority.write})
    void createUser(UserEntity user) {
        authUserDAO.createUser(user);
        userDataUserDAO.createUserInUserData(user);
    }

    @AfterEach
    void deleteUser() {
        UserEntityFromUserData userEntityFromUserData =
                userDataUserDAO.getUserByUserNameInUserData(userNameForTest);
        userDataUserDAO.deleteUserByIdInUserData(userEntityFromUserData.getId());

        UserEntity createdUser =
                authUserDAO.getUserByUserName(userNameForTest);
        authUserDAO.deleteUserById(createdUser.getId());

    }

    @Test
    @AllureId("1319990")
    @DBUser(
            username = userNameForTest,
            password = "12345",
            enabled = true,
            accountNonExpired = true,
            accountNonLocked = true,
            credentialsNonExpired = true,
            authorities = {Authority.read,Authority.write}
    )
    void authorizationWasCorrectTest(UserEntity user) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getUsername());
        $("input[name='password']").setValue(user.getPassword());
        $("button[type='submit']").click();
        $(".main-content__section-stats").should(visible);

        $x(".//li[@data-tooltip-content='Profile']").shouldBe(visible).click();
        $x(".//figcaption[.=\"" + user.getUsername() + "\"]").shouldBe(exist);


    }
}
