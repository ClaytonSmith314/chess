package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MemoryAuthDAO implements AuthDAO {

    private Collection<AuthData> authDataBase = new ArrayList<AuthData>();

    public void addAuth(AuthData authData) throws DataAccessException {
        for(var other: authDataBase) {
            if (other.username().equals(authData.username())) {
                throw new DataAccessException("Error: already taken");
            }
        }
        authDataBase.add(authData);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        for (var authData : authDataBase) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        throw new DataAccessException("Error: unauthorized");
    }

    public void removeAuth(AuthData authData) {
        authDataBase.remove(authData);
    }

    public Collection<AuthData> listAuth() {
        return new ArrayList<AuthData>(authDataBase);
    }

    public void clearAuth() {
        authDataBase = new ArrayList<AuthData>();
    }
    
}
