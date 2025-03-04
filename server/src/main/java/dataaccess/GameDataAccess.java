package dataaccess;

import java.util.ArrayList;
import java.util.Collection;

import model.GameData;


public class GameDataAccess {

    private ArrayList<GameData> gameDataBase = new ArrayList<GameData>();

    void addGame(GameData GameData) throws DataAccessException{
        for(var other: gameDataBase) {
            if (other.gameName().equals(GameData.gameName())
                ||other.gameID()==GameData.gameID()) {
                throw new DataAccessException("Error: bad request");
            }
        }
        gameDataBase.add(GameData);
    }

    GameData getGame(int gameID) throws DataAccessException {
        for (var gameData : gameDataBase) {
            if (gameData.gameID()==gameID) {
                return gameData;
            }
        }
        throw new DataAccessException("Error: bad request"); //TODO: unauthorized or incorrect Gamename?
    }

    void updateGame(GameData gameData) throws DataAccessException{
        for (int i=0; i<gameDataBase.size(); i++) {
            var toReplace = gameDataBase.get(i);
            if (toReplace.gameID()==gameData.gameID()) {
                gameDataBase.set(i, gameData);
                return;
            }
        }
        throw new DataAccessException("Error: bad request");
    }



    void deleteGame(GameData gameData) {
        gameDataBase.remove(gameData);
    }

    void clearAllGames() {
        gameDataBase = new ArrayList<GameData>();
    }

    
}
