package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

    @Test
    public void addUserTest(){
        UserDAO userDAO = new MemoryUserDAO();
        Assertions.assertDoesNotThrow(()->userDAO.addUser(johnUserData));
        Assertions.assertDoesNotThrow(()->userDAO.addUser(jamesUserData));

        Assertions.assertTrue(userDAO.listUsers().contains(johnUserData));
        Assertions.assertTrue(userDAO.listUsers().contains(jamesUserData));
    }
    @Test
    public void addUserUsernameTakenTest() throws DataAccessException{
        UserDAO userDAO = new MemoryUserDAO();
        userDAO.addUser(johnUserData);
        Assertions.assertThrows(DataAccessException.class, ()->userDAO.addUser(john2UserData));
    }

    @Test
    public void getUserTest() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        userDAO.addUser(johnUserData);
        userDAO.addUser(jamesUserData);

        Assertions.assertEquals(johnUserData, userDAO.getUser(johnUserData.username()));
        Assertions.assertEquals(jamesUserData, userDAO.getUser(jamesUserData.username()));
    }
    @Test
    public void getUserIncorrectUsernameTest() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        userDAO.addUser(johnUserData);
        userDAO.addUser(jamesUserData);

        Assertions.assertThrows(DataAccessException.class,
                ()->userDAO.getUser("notAUserName"));
    }

    @Test
    public void clearUsersTest() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        userDAO.addUser(johnUserData);
        userDAO.addUser(jamesUserData);

        userDAO.clearUsers();

        Assertions.assertTrue(userDAO.listUsers().isEmpty());
    }
}
