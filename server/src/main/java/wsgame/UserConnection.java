package wsgame;

import org.eclipse.jetty.websocket.api.Session;

public class UserConnection {

    Session mySession;
    GameRoom myGame;

    public UserConnection(Session session) {
        mySession = session;
    }

    public void onMessage(String message) {
        //TODO: Complete
    }

    public void connect() {

    }
}
