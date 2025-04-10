package wsgame;

import com.google.gson.Gson;
import model.GameId;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class UserConnection {

    private static final Gson serializer = new Gson();

    UserGameCommand.UserRole myRole;

    private final Session session;
    private GameRoom gameRoom;
    private GameId gameId;
    private String username;
    private String authToken;

    public UserConnection(Session session) {
        this.session = session;
    }

    public void onMessage(UserGameCommand userGameCommand) {
        switch(userGameCommand.getCommandType()) {
            case CONNECT -> {
                connect(userGameCommand);
            }
            case MAKE_MOVE -> {
            }
            case LEAVE -> {
            }
            case RESIGN -> {
            }
        }
    }

    public void sendString(String msg) {
        try {
            if (session.isOpen()) {
                session.getRemote().sendString(msg);
            }
        } catch(IOException ignored) {

        }
    }

    public void send(ServerMessage msg) {
        String string = serializer.toJson(msg, ServerMessage.class);
        sendString(string);
    }

    public void connect(UserGameCommand userGameCommand) {
        GameId gameId = new GameId(userGameCommand.getGameID());
        GameRoom gameRoom = GameRoom.fromGameId(gameId);
        if(gameRoom == null) {
            gameRoom = new GameRoom(gameId, this);
        }

        this.myRole = userGameCommand.userRole;
        this.gameRoom = gameRoom;
        this.authToken = userGameCommand.getAuthToken();
        this.username = userGameCommand.username;

        gameRoom.addUser(this, userGameCommand.userRole);
    }

    public void authorize() {

    }

    public String getUsername() {
        return username;
    }

}
