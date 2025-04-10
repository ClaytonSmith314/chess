package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import wsgame.UserConnection;

import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WSServer {

    private static final ConcurrentHashMap<Session, UserConnection> SESSION_TO_USER_CONNECTION = new ConcurrentHashMap<>();
    private static final Gson SERIALIZER = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand userGameCommand = SERIALIZER.fromJson(message, UserGameCommand.class);

        UserConnection userConnection = SESSION_TO_USER_CONNECTION.get(session);
        userConnection.onMessage(userGameCommand);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Session "+session+" has closed");
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket Error: " + error.getMessage());
        error.printStackTrace();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        SESSION_TO_USER_CONNECTION.put(session, new UserConnection(session));
    }
}
