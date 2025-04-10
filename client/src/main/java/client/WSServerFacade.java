package client;

import com.google.gson.Gson;
import ui.ChessUI;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WSServerFacade {

    private static final Gson serializer =  new Gson();

    private final WSClient wsClient;
    private final ChessUI chessUI;


    public WSServerFacade(ChessUI chessUI) throws Exception {
        wsClient = new WSClient(this);
        this.chessUI = chessUI;
    }

    public void handleMessage(String msgJson) {
        ServerMessage serverMessage = serializer.fromJson(msgJson, ServerMessage.class);
        switch(serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                chessUI.handleLoadGame(serverMessage);
            }
            case ERROR, NOTIFICATION -> {
                chessUI.handleServerNotificationOrError(serverMessage);
            }
        }
    }

    public void send(UserGameCommand command) throws Exception {
        String msg = serializer.toJson(command, UserGameCommand.class);
        wsClient.send(msg);
    }

    public void close() throws IOException {
        wsClient.stop();
    }


}
