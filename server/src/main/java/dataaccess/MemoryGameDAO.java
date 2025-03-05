package dataaccess;

import java.util.ArrayList;
import java.util.Collection;

import model.GameData;


public class MemoryGameDAO implements GameDAO {

    private ArrayList<GameData> gameDataBase = new ArrayList<GameData>();

    public void addGame(GameData GameData) throws DataAccessException{
        for(var other: gameDataBase) {
            if (other.gameName().equals(GameData.gameName())
                ||other.gameID()==GameData.gameID()) {
                throw new DataAccessException("Error: bad request");
            }
        }
        gameDataBase.add(GameData);
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
    
    public void removeGame(GameData gameData) {
        gameDataBase.remove(gameData);
    }

    public Collection<GameData> listGames() {
        return new ArrayList<GameData>(gameDataBase);
    }
    
    public void clearGames() {
        gameDataBase = new ArrayList<GameData>();
    }

    
}
