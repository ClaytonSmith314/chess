package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import dataaccess.SQLUserDAO;

public class UserDAOTests {


    private final UserData johnUserData = new UserData(
            "johndoe",
            "1234",
            "johndoe@notasite.com");
    private final UserData john2UserData = new UserData(
            "johndoe",
            "abcd",
            "johndoe2@notasite.com");

    private final UserData jamesUserData = new UserData(
            "jamessmith",
            "abcd",
            "jamessmith@notasite.com"
    );

    @BeforeAll
    public static void createDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Test
    @Order(0)
    public void verifyDbConnection() {
        Assertions.assertDoesNotThrow(()->{new SQLUserDAO();});
    }

    @Test
    @Order(1)
    public void addUserTest() throws DataAccessException{
        UserDAO userDAO = new SQLUserDAO();
        Assertions.assertDoesNotThrow(()->userDAO.addUser(johnUserData));
        Assertions.assertDoesNotThrow(()->userDAO.addUser(jamesUserData));

        Assertions.assertTrue(userDAO.listUsers().contains(johnUserData));
        Assertions.assertTrue(userDAO.listUsers().contains(jamesUserData));
    }
    @Test
    @Order(2)
    public void addUserUsernameTakenTest() throws DataAccessException{
        UserDAO userDAO = new SQLUserDAO();
        //johndoe username was already added in last test
        Assertions.assertThrows(DataAccessException.class, ()->userDAO.addUser(john2UserData));
    }

    @Test
    @Order(3)
    public void getUserTest() throws DataAccessException {
        UserDAO userDAO = new SQLUserDAO();

        Assertions.assertEquals(johnUserData, userDAO.getUser(johnUserData.username()));
        Assertions.assertEquals(jamesUserData, userDAO.getUser(jamesUserData.username()));
    }
    @Test
    @Order(4)
    public void getUserIncorrectUsernameTest() throws DataAccessException {
        UserDAO userDAO = new SQLUserDAO();

        Assertions.assertThrows(DataAccessException.class,
                ()->userDAO.getUser("notAUserName"));
    }

    @Test
    @Order(5)
    public void clearUsersTest() throws DataAccessException {
        UserDAO userDAO = new SQLUserDAO();
        userDAO.clearUsers();
        Assertions.assertTrue(userDAO.listUsers().isEmpty());
    }
}
