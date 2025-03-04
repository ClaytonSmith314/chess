package service;

import dataaccess.DataAccessException;
import model.*;

import spark.Request;
import spark.Response;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

public class Service {

    public void clear() throws DataAccessException{

    }

    public AuthData register(UserData userData) throws DataAccessException{
        return new AuthData("","");
    }

    public AuthData login(LoginData loginData) throws DataAccessException{
        return new AuthData("","");
    }

    public void logout(String authToken) throws DataAccessException{

    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException{
        return new ArrayList<GameData>();
    }

    public GameId createGame(String authToken, GameName name) throws DataAccessException{
        return new GameId(0);
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws DataAccessException{
    }

}
