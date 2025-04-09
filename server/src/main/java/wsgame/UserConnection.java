package wsgame;

import model.GameId;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;

public class UserConnection {

    public enum UserRole {
        WHITE_PLAYER,
        BLACK_PLAYER,
        OBSERVER
    }

    UserRole myRole;

    Session mySession;
    GameRoom myGame;
    GameId myGameId;
    String username;
    String authToken;

    public UserConnection(Session session) {
        mySession = session;
    }

    public void onMessage(UserGameCommand userGameCommand) {
        //TODO: Complete
    }

    public void connect(UserGameCommand userGameCommand) {
        GameId gameId = new GameId(userGameCommand.getGameID());
        GameRoom gameRoom = GameRoom.fromGameId(gameId);
        if(gameRoom == null) {
            gameRoom = new GameRoom(gameId);
        }
        gameRoom.

    }
}
