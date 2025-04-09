package wsgame;

import model.GameId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class GameRoom {

    private static final ConcurrentHashMap<GameId, GameRoom> gameIdsToGameRoom = new ConcurrentHashMap<>();

    public static GameRoom fromGameId(GameId gameId) {
        return gameIdsToGameRoom.get(gameId);
    }

    public GameRoom(GameId gameId) {
        gameIdsToGameRoom.put(gameId, this);
    }

    GameId gameId;

    UserConnection rootUser;

    UserConnection whiteUser;
    UserConnection blackUser;

    Collection<UserConnection> observers;

    Collection<UserConnection> connectedUsers = new ArrayList<>();

    public void addUser(UserConnection connection, UserConnection.UserRole role) {
        connectedUsers.add(connection);
    }

}
