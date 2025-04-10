package client;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WSServerFacade {

    private static final Gson serializer =  new Gson();

    private final WSClient wsClient;



    public WSServerFacade() throws Exception {
        wsClient = new WSClient(this);
    }

    public void handleMessage(String msgJson) {
        ServerMessage msg = serializer.fromJson(msgJson, ServerMessage.class);
        switch(msg.getServerMessageType()) {
            case LOAD_GAME -> {
            }
            case ERROR -> {
            }
            case NOTIFICATION -> {
                System.out.print("\n"+msg.notificationMsg);
            }
        }
    }

    public void send(UserGameCommand command) throws Exception {
        String msg = serializer.toJson(command, UserGameCommand.class);
        wsClient.send(msg);
    }


}
