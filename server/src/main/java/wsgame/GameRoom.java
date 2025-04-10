package wsgame;

import model.GameData;
import model.GameId;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class GameRoom {


    private static final ConcurrentHashMap<GameId, GameRoom> GAME_IDS_TO_GAME_ROOM = new ConcurrentHashMap<>();

    public static GameRoom fromGameId(GameId gameId) {
        return GAME_IDS_TO_GAME_ROOM.get(gameId);
    }

    public GameRoom(GameId gameId, UserConnection rootUser) {
        GAME_IDS_TO_GAME_ROOM.put(gameId, this);
        this.rootUser = rootUser;
        this.gameId = gameId;
    }

    private final GameId gameId;

    UserConnection rootUser;

    UserConnection whiteUser;
    UserConnection blackUser;

    private final Collection<UserConnection> observers = new ArrayList<>();

    private final Collection<UserConnection> connectedUsers = new ArrayList<>();

    public void addUser(UserConnection connection) {
        connectedUsers.add(connection);
        switch (connection.myRole) {
            case WHITE_PLAYER -> {
                whiteUser = connection;
                broadcastNotification("User " + connection.getUsername() + " joined as the white player.",
                connection);
            }
            case BLACK_PLAYER -> {
                blackUser = connection;
                broadcastNotification("User " + connection.getUsername() + " joined as the black player.",
                        connection);
            }
            case OBSERVER -> {
                observers.add(connection);
                broadcastNotification("User " + connection.getUsername() + " joined as an observer.",
                        connection);
            }
        }
    }

    public void removeUser(UserConnection connection) {
        if(Objects.equals(connection, whiteUser)) {
            whiteUser = null;
        }
        if(Objects.equals(connection, blackUser)) {
            blackUser = null;
        }
        if(Objects.equals(connection, rootUser)) {
            rootUser = null;
        }
        connectedUsers.remove(connection);
        observers.remove(connection);

        if(connectedUsers.isEmpty()) {
            GAME_IDS_TO_GAME_ROOM.remove(this.gameId);
        }

        broadcastNotification("User "+connection.getUsername()+" has left the game", connection);
    }

    public void broadcastNotification(String message, UserConnection excluded) {
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        serverMessage.message = message;
        broadcast(serverMessage, excluded);
    }
    public void broadcastGame(GameData gameData, UserConnection excluded) {
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        serverMessage.game = gameData;
        broadcast(serverMessage, excluded);
    }
    public void broadcast(ServerMessage serverMessage, UserConnection excluded) {
        for(var connection: connectedUsers) {
            if(connection==excluded) {
                continue;
            }
            connection.send(serverMessage);
        }
    }

}
