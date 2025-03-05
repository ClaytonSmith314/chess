package service;

import dataaccess.DataAccessException;
import model.*;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import dataaccess.*;

import javax.xml.crypto.Data;

public class Service {

    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();
    private UserDAO userDAO = new MemoryUserDAO();

    public void clear() throws DataAccessException{
        authDAO.clearAuth();
        gameDAO.clearGames();
        userDAO.clearUsers();
    }

    public AuthData register(UserData userData) throws DataAccessException{
        if(userData.password()==null || userData.username()==null || userData.email()==null)
            throw new DataAccessException("Error: bad request");
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

    public GamesList listGames(String authToken) throws DataAccessException{
        authDAO.getAuth(authToken);
        return new GamesList(gameDAO.listGames());
    }

    public GameId createGame(String authToken, GameName gameName) throws DataAccessException{
        AuthData authData = authDAO.getAuth(authToken);
        int gameId = generateGameId();
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameId, null, null, gameName.gameName(), chessGame);
        gameDAO.addGame(gameData);
        return new GameId(gameId);
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws DataAccessException{
        AuthData authData = authDAO.getAuth(authToken);
        GameData gameData = gameDAO.getGame(joinGameData.gameID());
        if(joinGameData.playerColor()==null)
            throw new DataAccessException("Error: bad request");
        if(joinGameData.playerColor().equals("WHITE")) {
            if(!(gameData.whiteUsername()==null))
                throw new DataAccessException("Error: already taken");
            gameData = new GameData(
                    gameData.gameID(),
                    authData.username(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            );
            gameDAO.updateGame(gameData);
        }
        else if(joinGameData.playerColor().equals("BLACK")) {
            if(!(gameData.blackUsername()==null))
                throw new DataAccessException("Error: already taken");
            gameData = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    authData.username(),
                    gameData.gameName(),
                    gameData.game()
            );
            gameDAO.updateGame(gameData);
        }
        else throw new DataAccessException("Error: bad request");
    }

    private static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    private static int nextGameId = 0;
    private static int generateGameId() {
        nextGameId += 1;
        return nextGameId;
    }

}
