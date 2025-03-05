package dataaccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO {

    void addUser(UserData userData) throws DataAccessException;

    UserData getUser(String userToken) throws DataAccessException;

    void updateUser(UserData userData) throws DataAccessException;

    void removeUser(UserData userData) throws DataAccessException;

    Collection<UserData> listUsers() throws DataAccessException;

    void clearUsers() throws DataAccessException;

}
