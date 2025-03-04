package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class AuthDataAccess {

    private Collection<AuthData> authDataBase = new ArrayList<AuthData>();

    void addAuth(AuthData authData) throws DataAccessException{
        for(var other: authDataBase) {
            if (other.username().equals(authData.username())) {
                throw new DataAccessException("Error: already taken");
            }
        }
        authDataBase.add(authData);
    }

    AuthData getAuth(String authToken) throws DataAccessException {
        for (var authData : authDataBase) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        throw new DataAccessException("Error: unauthorized");
    }

    void deleteAuth(AuthData authData) {
        authDataBase.remove(authData);
    }

    void clearAllAuths() {
        authDataBase = new ArrayList<AuthData>();
    }
    
}
