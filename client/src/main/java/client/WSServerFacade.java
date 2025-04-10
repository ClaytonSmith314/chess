package client;

import com.google.gson.Gson;
import ui.ChessUI;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WSServerFacade {

    private static final Gson SERIALIZER =  new Gson();

    private final WSClient wsClient;
    private final ChessUI chessUI;

    private boolean waitingForMessage = false;


    public WSServerFacade(ChessUI chessUI) throws Exception {
        wsClient = new WSClient(this);
        this.chessUI = chessUI;
    }

    public void handleMessage(String msgJson) {
        ServerMessage serverMessage = SERIALIZER.fromJson(msgJson, ServerMessage.class);
        switch(serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                chessUI.handleLoadGame(serverMessage);
            }
            case ERROR, NOTIFICATION -> {
                chessUI.handleServerNotificationOrError(serverMessage);
            }
        }
        waitingForMessage = false;
    }

    public void send(UserGameCommand command) throws Exception {
        String msg = SERIALIZER.toJson(command, UserGameCommand.class);
        wsClient.send(msg);
    }

    public void waitForMessage() {
        waitingForMessage = true;
        try {
            while (waitingForMessage) {
                Thread.sleep(80);
            }
        } catch(InterruptedException e) {
            waitingForMessage = false;
        }
    }

    public void sendAndWait(UserGameCommand command) throws Exception {
        send(command);
        waitForMessage();
    }

    public void close() throws IOException, InterruptedException {
        wsClient.stop();
    }


}
