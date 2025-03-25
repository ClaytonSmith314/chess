package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.Collection;


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

    private static GameId createdGameId1, createdGameId2;


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

        createdGameId1 = serverFacade.requestCreateGame(
                auth1.authToken(), new GameName("Game1"));

        createdGameId2 = serverFacade.requestCreateGame(
                auth2.authToken(), new GameName("Game2"));
    }


    @Test
    public void requestClearTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestClear());
        Assertions.assertTrue(serverFacade.requestListGames(auth1.authToken()).isEmpty());
    }

    @Test
    public void requestRegisterTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestRegister(notRegisteredUser));
    }
    @Test
    public void requestRegisterAlreadyRegisteredTest() {
        Assertions.assertThrows(HttpException.class,
                ()->serverFacade.requestRegister(loggedInUser1));
    }

    @Test
    public void requestLoginAfterLogoutTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestLogout(auth1.authToken()));
        Assertions.assertDoesNotThrow(()->serverFacade.requestLogin(
                loggedInUser1.getLoginData()));
    }
    @Test
    public void requestLoginAlreadyLoggedInTest() {
        Assertions.assertThrows(HttpException.class,
                ()->serverFacade.requestLogin(loggedInUser1.getLoginData()));
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
                new JoinGameData("WHITE", createdGameId1.gameID())
        ));
        Assertions.assertDoesNotThrow(()->serverFacade.requestJoinGame(
                auth2.authToken(),
                new JoinGameData("BLACK", createdGameId1.gameID())
        ));
    }
    @Test
    public void requestJoinGameSpotFilledTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.requestJoinGame(
                auth1.authToken(),
                new JoinGameData("WHITE", createdGameId1.gameID())
        ));
        Assertions.assertThrows(
                HttpException.class,
                ()->serverFacade.requestJoinGame(
                auth2.authToken(),
                new JoinGameData("WHITE", createdGameId1.gameID())
        ));
    }

}
