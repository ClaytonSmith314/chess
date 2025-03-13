package service;

import dataaccess.DataAccessException;
import model.*;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;

public class Service {

    private GameDAO gameDAO = new MemoryGameDAO();


    public void clear() throws DataAccessException{
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        authDAO.clearAuth();
        gameDAO.clearGames();
        userDAO.clearUsers();
    }

    public AuthData register(UserData userData) throws DataAccessException{
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        if(userData.password()==null || userData.username()==null || userData.email()==null) {
            throw new DataAccessException("Error: bad request");
        }
        UserData userDataWithHashedPassword = new UserData(
                userData.username(), hashPassword(userData.password()), userData.email()
        );
        userDAO.addUser(userDataWithHashedPassword);
        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, userData.username());
        authDAO.addAuth(authData);
        return authData;
    }

    public AuthData login(LoginData loginData) throws DataAccessException {
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        UserData userData = userDAO.getUser(loginData.username());
        if (!BCrypt.checkpw(loginData.password(), userData.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, userData.username());
        authDAO.addAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException{
        AuthDAO authDAO = new SQLAuthDAO();
        AuthData authData = authDAO.getAuth(authToken);
        authDAO.removeAuth(authData);
    }

    public GamesList listGames(String authToken) throws DataAccessException{
        AuthDAO authDAO = new SQLAuthDAO();
        authDAO.getAuth(authToken);
        return new GamesList(gameDAO.listGames());
    }

    public GameId createGame(String authToken, GameName gameName) throws DataAccessException{
        AuthDAO authDAO = new SQLAuthDAO();
        AuthData authData = authDAO.getAuth(authToken);
        int gameId = generateGameId();
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameId, null, null, gameName.gameName(), chessGame);
        gameDAO.addGame(gameData);
        return new GameId(gameId);
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws DataAccessException{
        AuthDAO authDAO = new SQLAuthDAO();
        AuthData authData = authDAO.getAuth(authToken);
        GameData gameData = gameDAO.getGame(joinGameData.gameID());
        if(joinGameData.playerColor()==null) {
            throw new DataAccessException("Error: bad request");
        }
        if(joinGameData.playerColor().equals("WHITE")) {
            if(!(gameData.whiteUsername()==null)) {
                throw new DataAccessException("Error: already taken");
            }
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
            if(!(gameData.blackUsername()==null)) {
                throw new DataAccessException("Error: already taken");
            }
            gameData = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    authData.username(),
                    gameData.gameName(),
                    gameData.game()
            );
            gameDAO.updateGame(gameData);
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }

    private static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    private static String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }


    private static int nextGameId = 0;
    private static int generateGameId() {
        nextGameId += 1;
        return nextGameId;
    }

}
