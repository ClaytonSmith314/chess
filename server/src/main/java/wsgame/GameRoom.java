package wsgame;

import model.GameId;

import java.util.Collection;

public class GameRoom {

    GameId gameId;

    UserConnection rootUser;

    UserConnection whiteUser;
    UserConnection blackUser;

    Collection<UserConnection> observers;

    public void onMessage(UserConnection connection, String message) {
        //TODO:
    }

}
