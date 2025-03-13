package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

    private Gson serializer = new Gson();

    public SQLGameDAO() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var createGameDataTable = """
                    CREATE TABLE  IF NOT EXISTS gameData (
                        gameID INT NOT NULL,
                        whiteUsername VARCHAR(255),
                        blackUsername VARCHAR(255),
                        gameName VARCHAR(255) NOT NULL,
                        game VARCHAR(4096) NOT NULL,
                        PRIMARY KEY (gameID)
                    )""";
            try (var createTableStatement = conn.prepareStatement(createGameDataTable)) {
                createTableStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void addGame(GameData gameData) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT 1 FROM gameData WHERE gameID=? LIMIT 1")) {
                preparedStatement.setInt(1, gameData.gameID());
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    throw new DataAccessException("Error: already taken");
                }
            }
            try (var preparedStatement = conn.prepareStatement(
                    """
                        INSERT INTO gameData (gameID, whiteUsername, 
                        blackUsername, gameName, game) VALUES(?, ?, ?, ?, ?)
                        """)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, gameData.whiteUsername());
                preparedStatement.setString(3, gameData.blackUsername());
                preparedStatement.setString(4, gameData.gameName());
                preparedStatement.setString(5, serializeGame(gameData.game()));
                preparedStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage()+": "+serializeGame(gameData.game()).length());
        }
    }

    public void updateGame(GameData gameData) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT 1 FROM gameData WHERE gameID=? LIMIT 1")) {
                preparedStatement.setInt(1, gameData.gameID());
                var rs = preparedStatement.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("Error: bad request");
                }
            }
            try (var preparedStatement = conn.prepareStatement(
                    "DELETE FROM gameData WHERE gameID=? LIMIT 1")) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.executeUpdate();
            }
            addGame(gameData);
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameID=? LIMIT 1")) {
                preparedStatement.setInt(1, gameID);
                try(var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        GameData gameData = new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                deserializeGame(rs.getString("game"))
                                );
                        return gameData;
                    } else {
                        throw new DataAccessException("Error: ungameorized");
                    }
                }
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }



    public Collection<GameData> listGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameData")) {
                try(var rs = preparedStatement.executeQuery()) {
                    ArrayList<GameData> gameDataList = new ArrayList<>();
                    while (rs.next()) {
                        GameData gameData = new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                deserializeGame(rs.getString("game"))
                        );
                        gameDataList.add(gameData);
                    }
                    return gameDataList;
                }
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clearGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var clearTableMsg = "TRUNCATE TABLE gameData";
            try (var clearTableStatement = conn.prepareStatement(clearTableMsg)) {
                clearTableStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    
    private String serializeGame(ChessGame game) {
        return serializer.toJson(game);
    }

    private ChessGame deserializeGame(String serializedGame) {
        return serializer.fromJson(serializedGame, ChessGame.class);
    }

}
