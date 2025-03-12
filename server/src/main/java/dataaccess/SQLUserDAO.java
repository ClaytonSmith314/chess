package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var createUserDataTable = """
                    CREATE TABLE  IF NOT EXISTS userData (
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        PRIMARY KEY (username)
                    )""";
            try (var createTableStatement = conn.prepareStatement(createUserDataTable)) {
                createTableStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void addUser(UserData userData) throws DataAccessException{

    }

    public UserData getUser(String username) throws DataAccessException {
        return new UserData("","","");
    }

    public Collection<UserData> listUsers() {
        return new ArrayList<UserData>();
    }

    public void clearUsers() {
    }

}
