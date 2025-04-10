package wsgame;

import model.GameId;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class GameRoom {


    private static final ConcurrentHashMap<GameId, GameRoom> gameIdsToGameRoom = new ConcurrentHashMap<>();

    public static GameRoom fromGameId(GameId gameId) {
        return gameIdsToGameRoom.get(gameId);
    }

    public GameRoom(GameId gameId, UserConnection rootUser) {
        gameIdsToGameRoom.put(gameId, this);
        this.rootUser = rootUser;
        this.gameId = gameId;
    }

    private final GameId gameId;

    UserConnection rootUser;

    UserConnection whiteUser;
    UserConnection blackUser;

    Collection<UserConnection> observers;

    Collection<UserConnection> connectedUsers = new ArrayList<>();

    public void addUser(UserConnection connection, UserGameCommand.UserRole role) {
        connectedUsers.add(connection);
        if(role!=null) {
            switch (role) {
                case WHITE_PLAYER -> {
                    whiteUser = connection;
                    broadcastNotification("User " + connection.getUsername() + " joined as the white player.");
                }
                case BLACK_PLAYER -> {
                    blackUser = connection;
                    broadcastNotification("User " + connection.getUsername() + " joined as the black player.");
                }
                case OBSERVER -> {
                    observers.add(connection);
                    broadcastNotification("User " + connection.getUsername() + " joined as an observer.");
                }
            }
        }
    }

    public void broadcastNotification(String message) {
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        serverMessage.notificationMsg = message;

        for(var connection: connectedUsers) {
            connection.send(serverMessage);
        }
    }

}
