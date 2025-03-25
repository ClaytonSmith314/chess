package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;


    private static UserData loggedInUser1 = new UserData(
            "JohnDoe", "1234", "johndoe@notasite.com"
    );
    private static AuthData auth1;
    private static UserData loggedInUser2 = new UserData(
            "JaneSmith", "abcd", "janesmith@notasite.com"
    );
    private static AuthData auth2;

    private static UserData notRegisteredUser = new UserData(
            "AdamThompson", "0987", "adamthompson@notasite.com"
    );

    private static GameId createdGameId;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setupServerDatabase() throws HttpException {
        serverFacade.requestClear();

        auth1 = serverFacade.requestRegister(loggedInUser1);
        auth2 = serverFacade.requestRegister(loggedInUser2);

        createdGameId = serverFacade.requestCreateGame(
                auth1.authToken(), new GameName("Game1"));
    }



    @Test
    public void requestClearTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestClear());
        Assertions.assertTrue(serverFacade.requestListGames(auth1.authToken()).isEmpty());
    }

    @Test
    public void requestRegisterTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestRegister(notRegisteredUser));
//        Assertions.assertEquals("", serverFacade.requestRegister(notRegisteredUser).authToken());
    }
    @Test
    public void requestRegisterAlreadyRegisteredTest() {
        Assertions.assertThrows(HttpException.class,
                ()->serverFacade.requestRegister(loggedInUser1));
    }

    @Test
    public void requestLoginTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestLogin(
                loggedInUser1.getLoginData()));
    }
    @Test
    public void requestLoginWrongUsernameAndPasswordTest() {
        Assertions.assertThrows(HttpException.class,
                ()->serverFacade.requestLogin(notRegisteredUser.getLoginData()));
    }

    @Test
    public void requestLogoutTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestLogout(auth1.authToken()));
    }
    @Test
    public void requestLogoutBadAuthTokenTest() {
        Assertions.assertThrows(HttpException.class,
                ()->serverFacade.requestLogout("notanauthtoken"));
    }

    @Test
    public void requestListGamesTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestListGames(auth1.authToken()));
        boolean containsGameId = false;
        for(var gameData:serverFacade.requestListGames(auth1.authToken())) {
            if (gameData.gameID()==createdGameId.gameID()) {
                containsGameId = true;
            }
        }
        Assertions.assertTrue(containsGameId);
    }
    @Test
    public void requestListGamesBadAuth() {
        Assertions.assertThrows(HttpException.class,
                ()->serverFacade.requestListGames("badauth"));
    }

    @Test
    public void requestCreateGameTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestCreateGame(
                auth1.authToken(), new GameName("Game3")));
    }
    @Test
    public void requestCreateGameNameTakenTest() {
        Assertions.assertThrows(HttpException.class,
                ()->serverFacade.requestCreateGame(auth2.authToken(), new GameName("Game1")));
    }

    @Test
    public void requestJoinGameTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestJoinGame(
                auth1.authToken(),
                new JoinGameData("WHITE", createdGameId.gameID())
        ));
        Assertions.assertDoesNotThrow(()->serverFacade.requestJoinGame(
                auth2.authToken(),
                new JoinGameData("BLACK", createdGameId.gameID())
        ));
    }
    @Test
    public void requestJoinGameSpotFilledTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestJoinGame(
                auth1.authToken(),
                new JoinGameData("WHITE", createdGameId.gameID())
        ));
        Assertions.assertThrows(
                HttpException.class,
                ()->serverFacade.requestJoinGame(
                auth2.authToken(),
                new JoinGameData("WHITE", createdGameId.gameID())
        ));
    }

}
