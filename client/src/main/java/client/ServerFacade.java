package client;

import com.google.gson.Gson;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class ServerFacade {

    private final HttpClient client;
    private final Gson serializer = new Gson();

    public ServerFacade(int port) {
        client = new HttpClient("http://localhost:"+port);
    }

    void requestClear() throws HttpException {
        client.sendHttpRequest("/db", HttpClient.DELETE, null, null);
    }
    
    AuthData requestRegister(UserData userData) throws HttpException {
        String body = serializer.toJson(userData);
        String resp = client.sendHttpRequest("/user", HttpClient.POST, null, body);
        return serializer.fromJson(resp, AuthData.class);
    }
    
    AuthData requestLogin(LoginData loginData) throws HttpException {
        String body = serializer.toJson(loginData);
        String resp = client.sendHttpRequest("/user", HttpClient.POST, null, body);
        return serializer.fromJson(resp, AuthData.class);
    }

    void requestLogout(String authToken) throws HttpException {
        client.sendHttpRequest("/user", HttpClient.POST, authToken, null);
    }
    
    Collection<GameData> requestListGames(String authToken) throws HttpException {
        return new ArrayList<GameData>();
    }
    
    GameId requestCreateGame(String authToken, GameName gameName) throws HttpException {
        return new GameId(0);
    }
    
    void requestJoinGame(String authToken, JoinGameData joinGameData) throws HttpException {
        
    }

}
