package client;

import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class ServerFacade {

    public ServerFacade(int port) {

    }

    void requestClear() throws HttpException {
        
    }
    
    AuthData requestRegister(UserData userData) throws HttpException {
        return new AuthData("","");
    }
    
    AuthData requestLogin(LoginData loginData) throws HttpException {
        return new AuthData("","");
    }

    void requestLogout(String authToken) throws HttpException {
        
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
