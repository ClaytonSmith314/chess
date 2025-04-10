package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import wsgame.UserConnection;

import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WSServer {

    private static final ConcurrentHashMap<Session, UserConnection> sessionToUserConnection = new ConcurrentHashMap<>();
    private static final Gson serializer = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand userGameCommand = serializer.fromJson(message, UserGameCommand.class);

        UserConnection userConnection = sessionToUserConnection.get(session);
        userConnection.onMessage(userGameCommand);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {

    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        sessionToUserConnection.put(session, new UserConnection(session));
    }
}
