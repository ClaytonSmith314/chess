package server;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Spark;
import wsgame.UserConnection;

import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WSServer {

    private static final ConcurrentHashMap<Session, UserConnection> sessionToUserConnection = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Spark.port(8080);
        Spark.webSocket("/ws", WSServer.class);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserConnection userConnection = sessionToUserConnection.get(session);
        userConnection.onMessage(message);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        //TODO: finish block
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        sessionToUserConnection.put(session, new UserConnection(session));
    }
}
