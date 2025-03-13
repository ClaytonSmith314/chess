package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;

public class GameDAOTests {

    GameData game1 = new GameData(
            1,
            "whiteUser1",
            null,
            "game1",
            new ChessGame()
    );
    GameData game1Update = new GameData(
            1,
            "whiteUser1",
            "blackUser1",
            "game1",
            game1.game()
    );
    GameData game1UpdateBadId = new GameData(
            10,
            "whiteUser1",
            "blackUser1",
            "game1",
            game1.game()
    );
    GameData game2 = new GameData(
            2,
            "whiteUser1",
            "blackUser1",
            "game2",
            new ChessGame()
    );
    GameData game3 = new GameData(
            1,
            "whiteUser1",
            "blackUser1",
            "game1",
            new ChessGame()
    );
    

    @BeforeAll
    public static void createDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @BeforeEach
    public void clearBeforeTests() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        gameDAO.clearGames();
    }

    @Test
    @Order(1)
    public void verifyDbConnection() {
        Assertions.assertDoesNotThrow(()->{new SQLGameDAO();});
    }

    @Test
    @Order(2)
    public void addGameTest() throws DataAccessException{
        GameDAO gameDAO = new SQLGameDAO();
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game1));
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game2));

        Assertions.assertTrue(gameDAO.listGames().contains(game1));
        Assertions.assertTrue(gameDAO.listGames().contains(game2));
    }
    @Test
    @Order(3)
    public void addGameIdTakenTest() throws DataAccessException{
        GameDAO gameDAO = new SQLGameDAO();

        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game1));
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game2));
        Assertions.assertTrue(gameDAO.listGames().contains(game1));
        Assertions.assertThrows(DataAccessException.class, ()->gameDAO.addGame(game3));
    }

    @Test
    @Order(4)
    public void getGameTest() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game1));
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game2));
        Assertions.assertEquals(game1, gameDAO.getGame(game1.gameID()));
        Assertions.assertEquals(game2, gameDAO.getGame(game2.gameID()));
    }
    @Test
    @Order(5)
    public void getGameIncorrectGameIdTest() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game1));
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game2));
        Assertions.assertThrows(DataAccessException.class,
                ()->gameDAO.getGame(10));
    }

    @Test
    @Order(6)
    public void updateGameTest() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game1));
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game2));
        Assertions.assertDoesNotThrow(()->gameDAO.updateGame(game1Update));
        Assertions.assertTrue(gameDAO.listGames().contains(game1Update));
        Assertions.assertFalse(gameDAO.listGames().contains(game1));
    }
    @Test
    @Order(7)
    public void updateGameIncorrectGameIdTest() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game1));
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game2));
        Assertions.assertThrows(DataAccessException.class,
                ()->gameDAO.updateGame(game1UpdateBadId));
    }

    @Test
    @Order(8)
    public void clearGamesTest() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game1));
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game2));
        gameDAO.clearGames();
        Assertions.assertTrue(gameDAO.listGames().isEmpty());
    }

}
