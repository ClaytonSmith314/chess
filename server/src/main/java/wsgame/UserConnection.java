package wsgame;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.GameId;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.Objects;

import dataaccess.*;

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
        try {
            switch (userGameCommand.getCommandType()) {
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
        } catch(DataAccessException dataAccessException) {
            ServerMessage errMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errMsg.message = dataAccessException.getMessage();
            send(errMsg);
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

    public void connect(UserGameCommand userGameCommand) throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();
        AuthData authData = authDAO.getAuth(userGameCommand.getAuthToken());
        GameData gameData = gameDAO.getGame(userGameCommand.getGameID());

        GameId gameId = new GameId(gameData.gameID());
        GameRoom gameRoom = GameRoom.fromGameId(gameId);
        if(gameRoom == null) {
            gameRoom = new GameRoom(gameId, this);
        }

        if(Objects.equals(gameData.whiteUsername(), authData.username())) {
            this.myRole = UserGameCommand.UserRole.WHITE_PLAYER;
        } else if(Objects.equals(gameData.blackUsername(), authData.username())) {
            this.myRole = UserGameCommand.UserRole.BLACK_PLAYER;
        } else {
            this.myRole = UserGameCommand.UserRole.OBSERVER;
        }

        this.username = authData.username();
        this.authToken = authData.authToken();
        this.gameRoom = gameRoom;

        gameRoom.addUser(this);

        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        message.game = gameData.game().getBoard();
        send(message);
    }


    public String getUsername() {
        return username;
    }

}
