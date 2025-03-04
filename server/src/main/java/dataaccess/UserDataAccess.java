package dataaccess;

import java.util.ArrayList;
import java.util.Collection;

import model.UserData;

import javax.xml.crypto.Data;

public class UserDataAccess {

    private Collection<UserData> userDataBase = new ArrayList<UserData>();

    void addUser(UserData userData) throws DataAccessException{
        for(var other: userDataBase) {
            if (other.username().equals(userData.username())) {
                throw new DataAccessException("Error: already taken");
            }
        }
        userDataBase.add(userData);
    }

    UserData getUser(String username) throws DataAccessException {
        for (var userData : userDataBase) {
            if (userData.username().equals(username)) {
                return userData;
            }
        }
        throw new DataAccessException("Error: unauthorized"); //TODO: unauthorized or incorrect username?
    }

    void deleteUser(UserData userData) {
        userDataBase.remove(userData);
    }

    void clearAllUsers() {
        userDataBase = new ArrayList<UserData>();
    }


}
