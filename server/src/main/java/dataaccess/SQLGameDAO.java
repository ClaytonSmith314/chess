package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO {
    private ArrayList<GameData> gameDataBase = new ArrayList<GameData>();

    public void addGame(GameData gameData) throws DataAccessException{
        for(var other: gameDataBase) {
            if (other.gameName().equals(gameData.gameName())
                    ||other.gameID()==gameData.gameID()) {
                throw new DataAccessException("Error: bad request");
            }
        }
        gameDataBase.add(gameData);
    }

    public void updateGame(GameData gameData) throws DataAccessException{
        for (int i=0; i<gameDataBase.size(); i++) {
            var toReplace = gameDataBase.get(i);
            if (toReplace.gameID()==gameData.gameID()) {
                gameDataBase.set(i, gameData);
                return;
            }
        }
        throw new DataAccessException("Error: bad request");
    }

    public GameData getGame(int gameID) throws DataAccessException {
        for (var gameData : gameDataBase) {
            if (gameData.gameID()==gameID) {
                return gameData;
            }
        }
        throw new DataAccessException("Error: bad request");
    }

    public Collection<GameData> listGames() {
        return new ArrayList<GameData>(gameDataBase);
    }

    public void clearGames() {
        gameDataBase = new ArrayList<GameData>();
    }

}
