package client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class ServerFacade {

    private final HttpClient client;
    private final Gson serializer = new Gson();

    public ServerFacade(int port) {
        client = new HttpClient("http://localhost:"+port);
    }

    public void requestClear() throws HttpException {
        client.sendHttpRequest("/db", HttpClient.DELETE, null, null);
    }
    
    public AuthData requestRegister(UserData userData) throws HttpException {
        String body = serializer.toJson(userData);
        String resp = client.sendHttpRequest("/user", HttpClient.POST, null, body);
        return serializer.fromJson(resp, AuthData.class);
    }
    
    public AuthData requestLogin(LoginData loginData) throws HttpException {
        String body = serializer.toJson(loginData);
        String resp = client.sendHttpRequest("/session", HttpClient.POST, null, body);
        return serializer.fromJson(resp, AuthData.class);
    }

    public void requestLogout(String authToken) throws HttpException {
        client.sendHttpRequest("/session", HttpClient.DELETE, authToken, null);
    }
    
    public Collection<GameData> requestListGames(String authToken) throws HttpException {
        String resp = client.sendHttpRequest("/game", HttpClient.GET, authToken, null);
        return serializer.fromJson(resp, GamesList.class).games();
    }
    
    public GameId requestCreateGame(String authToken, GameName gameName) throws HttpException {
        String body = serializer.toJson(gameName);
        String resp = client.sendHttpRequest("/game", HttpClient.POST, authToken, body);
        return serializer.fromJson(resp, GameId.class);
    }
    
    public void requestJoinGame(String authToken, JoinGameData joinGameData) throws HttpException {
        String body = serializer.toJson(joinGameData);
        client.sendHttpRequest("/game", HttpClient.PUT, authToken, body);
    }

}
