package dataaccess;

import model.UserData;

import java.sql.Array;
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
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT 1 FROM userData WHERE username=? LIMIT 1")) {
                preparedStatement.setString(1, userData.username());
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    throw new DataAccessException("Error: already taken");
                }
            }
            try (var preparedStatement = conn.prepareStatement(
                    "INSERT INTO userData (username, password, email) VALUES(?, ?, ?)")) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT username, password, email FROM userData WHERE username=?")) {
                preparedStatement.setString(1, username);
                try(var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        UserData userData = new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email"));
                        return userData;
                    } else {
                        throw new DataAccessException("\"Error: unauthorized\"");
                    }
                }
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Collection<UserData> listUsers() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT username, password, email FROM userData")) {
                try(var rs = preparedStatement.executeQuery()) {
                    ArrayList<UserData> userDataList = new ArrayList<>();
                    while (rs.next()) {
                        UserData userData = new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email"));
                        userDataList.add(userData);
                    }
                    return userDataList;
                }
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clearUsers() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var clearTableMsg = "TRUNCATE TABLE userData";
            try (var clearTableStatement = conn.prepareStatement(clearTableMsg)) {
                clearTableStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
