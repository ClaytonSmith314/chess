package service;

import dataaccess.DataAccessException;
import model.*;

import spark.Request;
import spark.Response;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

public class Service {

    public void clear(){

    }

    public AuthData register(UserData userData){
        return new AuthData("","");
    }

    public AuthData login(LoginData loginData){
        return new AuthData("","");
    }

    public void logout(String authToken){

    }

    public Collection<GameData> listGames(String authToken){
        return new ArrayList<GameData>();
    }

    public GameId createGame(String authToken, GameName name){
        return new GameId(0);
    }

    public void joinGame(String authToken, JoinGameData joinGameData) {
    }

}
