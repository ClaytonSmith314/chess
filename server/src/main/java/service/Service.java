package service;

import dataaccess.DataAccessException;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import dataaccess.*;

public class Service {

    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();
    private UserDAO userDAO = new MemoryUserDAO();

    public void clear() throws DataAccessException{

    }

    public AuthData register(UserData userData) throws DataAccessException{
        userDAO.addUser(userData);
        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, userData.username());
        authDAO.addAuth(authData);
        return authData;
    }

    public AuthData login(LoginData loginData) throws DataAccessException {
        UserData userData = userDAO.getUser(loginData.username());
        if (!userData.password().equals(loginData.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, userData.username());
        authDAO.addAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException{
        AuthData authData = authDAO.getAuth(authToken);
        authDAO.removeAuth(authData);
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException{
        return new ArrayList<GameData>();
    }

    public GameId createGame(String authToken, GameName name) throws DataAccessException{
        return new GameId(0);
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws DataAccessException{
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
