package dataaccess;

import java.util.ArrayList;
import java.util.Collection;

import model.UserData;

public class MemoryUserDAO implements UserDAO {

    private Collection<UserData> userDataBase = new ArrayList<UserData>();

    public void addUser(UserData userData) throws DataAccessException{
        for(var other: userDataBase) {
            if (other.username().equals(userData.username())) {
                throw new DataAccessException("Error: already taken");
            }
        }
        userDataBase.add(userData);
    }

    public UserData getUser(String username) throws DataAccessException {
        for (var userData : userDataBase) {
            if (userData.username().equals(username)) {
                return userData;
            }
        }
        throw new DataAccessException("Error: unauthorized"); //TODO: unauthorized or incorrect username?
    }

    public void clearUsers() {
        userDataBase = new ArrayList<UserData>();
    }


}
