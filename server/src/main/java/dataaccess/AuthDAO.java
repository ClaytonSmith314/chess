package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO {

    void addAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void removeAuth(AuthData authData) throws DataAccessException;

    void clearAuth() throws DataAccessException;
}
