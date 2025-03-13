package service;

import chess.ChessPiece;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import service.Service;
import dataaccess.DataAccessException;

public class ServiceTests {


    @BeforeEach
    public void cleardb() throws DataAccessException{
        Service service = new Service();
        service.clear();
    }

    @Test
    public void testRegisterNoException() {
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        Assertions.assertDoesNotThrow(()->service.register(userData));
    }
    @Test
    public void testRegisterUsernameTaken() {
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        Assertions.assertDoesNotThrow(()->service.register(userData));
        var userDataWithUsernameTaken = new UserData(
                "johndoe",
                "catfish",
                "johndoe2@notasite.org");
        Assertions.assertThrows(
                DataAccessException.class,
                ()->service.register(userDataWithUsernameTaken)
        );
    }



    @Test
    public void testLoginNoException() {
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        Assertions.assertDoesNotThrow(()->service.register(userData));
        var loginData = new LoginData(
                "johndoe",
                "1234"
        );
        Assertions.assertDoesNotThrow(()->service.login(loginData));
    }
    @Test
    public void testLoginWrongPassword() {
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        Assertions.assertDoesNotThrow(()->service.register(userData));
        var loginData = new LoginData(
                "johndoe",
                "1239"
        );
        Assertions.assertThrows(
                DataAccessException.class,
                ()->service.login(loginData)
        );
    }
    @Test
    public void testLoginWrongUsername() {
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        Assertions.assertDoesNotThrow(()->service.register(userData));
        var loginData = new LoginData(
                "JohnDoe",
                "1234"
        );
        Assertions.assertThrows(
                DataAccessException.class,
                ()->service.login(loginData)
        );
    }



    @Test
    public void testLogoutNoException() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        Assertions.assertDoesNotThrow(()->service.logout(authData.authToken()));
    }
    @Test
    public void testLogoutWrongAuthToken() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        Assertions.assertThrows(
                DataAccessException.class,
                ()->service.logout("badAuthString")
        );
    }
    @Test
    public void testLogoutTwiceThrowsError() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        Assertions.assertDoesNotThrow(()->service.logout(authData.authToken()));
        Assertions.assertThrows(
                DataAccessException.class,
                ()->service.logout(authData.authToken())
        );
    }



    @Test
    public void testCreateGameNoException() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        Assertions.assertDoesNotThrow(()->service.createGame(authData.authToken(),
                new GameName("new_game")));
    }
    @Test
    public void testCreateGameNameTaken() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        service.createGame(authData.authToken(), new GameName("new_game"));

        Assertions.assertThrows(
                DataAccessException.class,
                ()->service.createGame(authData.authToken(),
                        new GameName("new_game"))
        );
    }
    @Test
    public void testCreateGameBadAuthToken(){
        Service service = new Service();

        Assertions.assertThrows(
                DataAccessException.class,
                ()->service.createGame("badAuthString",
                        new GameName("new_game"))
        );
    }



    //joinGame Tests
    @Test
    public void testJoinGameNoException() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        GameId gameId = service.createGame(authData.authToken(),
                new GameName("new_game"));
        
        JoinGameData joinGameData = new JoinGameData("BLACK", gameId.gameID());
        
        Assertions.assertDoesNotThrow(()->service.joinGame(authData.authToken(), joinGameData));
        
    }
    @Test
    public void testJoinGameIdDoesNotExist() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        service.createGame(authData.authToken(), new GameName("new_game"));

        JoinGameData joinGameData = new JoinGameData("BLACK", 1010);

        Assertions.assertThrows(
                DataAccessException.class,
                ()->service.joinGame(authData.authToken(),
                        joinGameData)
        );
    }
    @Test
    public void testJoinGameBadAuthToken() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);
        GameId gameId = service.createGame(authData.authToken(),
                new GameName("new_game"));

        JoinGameData joinGameData = new JoinGameData("BLACK", gameId.gameID());


        Assertions.assertThrows(
                DataAccessException.class,
                ()->service.joinGame("badAuthString",
                        joinGameData)
        );
    }




    @Test
    void testListGamesNoErrors() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        GameId gameId1 = service.createGame(authData.authToken(),
                new GameName("game1"));
        GameId gameId2 = service.createGame(authData.authToken(),
                new GameName("game2"));
        GameId gameId3 = service.createGame(authData.authToken(),
                new GameName("game3"));

        var gamesList = service.listGames(authData.authToken());

        Assertions.assertEquals(3, gamesList.games().size());

        boolean gameListContainsId1 = false;
        boolean gameListContainsId2 = false;
        boolean gameListContainsId3 = false;
        for(var gameData : gamesList.games()) {
            if(gameData.gameID() == gameId1.gameID()) { gameListContainsId1 = true; }
            if(gameData.gameID() == gameId2.gameID()) { gameListContainsId2 = true; }
            if(gameData.gameID() == gameId3.gameID()) { gameListContainsId3 = true; }
        }
        Assertions.assertTrue(gameListContainsId1);
        Assertions.assertTrue(gameListContainsId2);
        Assertions.assertTrue(gameListContainsId3);
    }
    @Test
    public void testListGamesBadAuthToken(){
        Service service = new Service();

        Assertions.assertThrows(
                DataAccessException.class,
                ()->service.listGames("badAuthString")
        );
    }


    @Test
    void testClearUserData() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        service.clear();

        Assertions.assertThrows(DataAccessException.class,
                ()->service.login(new LoginData("johndoe","1234")));
    }
    @Test
    void testClearAuthData() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        service.clear();

        Assertions.assertThrows(DataAccessException.class,
                ()->service.logout(authData.authToken()));
    }
    @Test
    void testClearGameData() throws DataAccessException{
        Service service = new Service();
        var userData = new UserData(
                "johndoe",
                "1234",
                "johndoe@notasite.org");
        AuthData authData = service.register(userData);

        GameId gameId1 = service.createGame(authData.authToken(),
                new GameName("game1"));
        GameId gameId2 = service.createGame(authData.authToken(),
                new GameName("game2"));
        GameId gameId3 = service.createGame(authData.authToken(),
                new GameName("game3"));

        service.clear();

        AuthData newAuthData = service.register(userData);

        var gamesList = service.listGames(newAuthData.authToken());

        Assertions.assertTrue(gamesList.games().isEmpty());
    }

}
