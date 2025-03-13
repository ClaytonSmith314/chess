package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

public class AuthDAOTests {

    private final AuthData johnAuthData = new AuthData(
            "authtoken",
            "johndoe");
    private final AuthData john2AuthData = new AuthData(
            "authtoken",
            "johndoe2");
    private final AuthData jamesAuthData = new AuthData(
            "authtoken2",
            "jamessmith");

    @BeforeAll
    public static void createDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @BeforeEach
    public void clearBeforeTests() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();
        authDAO.clearAuth();
    }

    @Test
    @Order(1)
    public void verifyDbConnection() {
        Assertions.assertDoesNotThrow(()->{new SQLAuthDAO();});
    }

    @Test
    @Order(2)
    public void addAuthTest() throws DataAccessException{
        AuthDAO authDAO = new SQLAuthDAO();
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(johnAuthData));
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(jamesAuthData));

        Assertions.assertTrue(authDAO.listAuth().contains(johnAuthData));
        Assertions.assertTrue(authDAO.listAuth().contains(jamesAuthData));
    }
    @Test
    @Order(3)
    public void addAuthTokenTakenTest() throws DataAccessException{
        AuthDAO authDAO = new SQLAuthDAO();

        Assertions.assertDoesNotThrow(()->authDAO.addAuth(johnAuthData));
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(jamesAuthData));
        Assertions.assertTrue(authDAO.listAuth().contains(johnAuthData));
        Assertions.assertThrows(DataAccessException.class, ()->authDAO.addAuth(john2AuthData));
    }

    @Test
    @Order(4)
    public void getAuthTest() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(johnAuthData));
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(jamesAuthData));
        Assertions.assertEquals(johnAuthData, authDAO.getAuth(johnAuthData.authToken()));
        Assertions.assertEquals(jamesAuthData, authDAO.getAuth(jamesAuthData.authToken()));
    }
    @Test
    @Order(5)
    public void getAuthIncorrectAuthTokenTest() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(johnAuthData));
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(jamesAuthData));
        Assertions.assertThrows(DataAccessException.class,
                ()->authDAO.getAuth("notAAuthToken"));
    }
    
    @Test
    @Order(6)
    public void removeAuthTest() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(johnAuthData));
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(jamesAuthData));
        Assertions.assertDoesNotThrow(()->authDAO.removeAuth(johnAuthData));
        Assertions.assertFalse(authDAO.listAuth().contains(johnAuthData));
    }
    @Test
    @Order(7)
    public void removeAuthIncorrectAuthTokenTest() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(johnAuthData));
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(jamesAuthData));
        authDAO.removeAuth(john2AuthData);
    }
    
    @Test
    @Order(8)
    public void clearAuthsTest() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(johnAuthData));
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(jamesAuthData));
        authDAO.clearAuth();
        Assertions.assertTrue(authDAO.listAuth().isEmpty());
    }



}
