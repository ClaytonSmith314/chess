package dataaccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO {

    void addUser(UserData userData) throws DataAccessException;

    UserData getUser(String userToken) throws DataAccessException;

    void clearUsers() throws DataAccessException;

}
