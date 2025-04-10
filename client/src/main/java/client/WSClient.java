package client;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WSClient extends Endpoint {

    public Session session;

    public WSServerFacade wsServerFacade;

    public WSClient(WSServerFacade wsServerFacade) throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.wsServerFacade = wsServerFacade;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                wsServerFacade.handleMessage(message);
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void stop() throws IOException, InterruptedException {
        session.wait(300);
        session.close();
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
