package dataaccess;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.AuthData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var createAuthDataTable = """
                    CREATE TABLE  IF NOT EXISTS authData (
                        authToken VARCHAR(255) NOT NULL,
                        username VARCHAR(255) NOT NULL,
                        PRIMARY KEY (authToken)
                    )""";
            try (var createTableStatement = conn.prepareStatement(createAuthDataTable)) {
                createTableStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void addAuth(AuthData authData) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT 1 FROM authData WHERE BINARY authToken=? LIMIT 1")) {
                preparedStatement.setString(1, authData.authToken());
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    throw new DataAccessException("Error: already taken");
                }
            }
            try (var preparedStatement = conn.prepareStatement(
                    "INSERT INTO authData (authToken, username) VALUES(?, ?)")) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                preparedStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT authToken, username FROM authData WHERE BINARY authToken=? LIMIT 1")) {
                preparedStatement.setString(1, authToken);
                try(var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        AuthData authData = new AuthData(
                                rs.getString("authToken"),
                                rs.getString("username"));
                        return authData;
                    } else {
                        throw new DataAccessException("Error: unauthorized");
                    }
                }
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Collection<AuthData> listAuth() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT authToken, username FROM authData")) {
                try(var rs = preparedStatement.executeQuery()) {
                    ArrayList<AuthData> authDataList = new ArrayList<>();
                    while (rs.next()) {
                        AuthData authData = new AuthData(
                                rs.getString("authToken"),
                                rs.getString("username"));
                        authDataList.add(authData);
                    }
                    return authDataList;
                }
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void removeAuth(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
//            try (var preparedStatement = conn.prepareStatement(
//                    "SELECT 1 FROM authData WHERE BINARY authToken=? LIMIT 1")) {
//                preparedStatement.setString(1, authData.authToken());
//                var rs = preparedStatement.executeQuery();
//                if (rs.next()) {
//                    throw new DataAccessException("Error: already taken");
//                }
//            }
            try (var preparedStatement = conn.prepareStatement(
                    "DELETE FROM authData WHERE BINARY authToken=? LIMIT 1")) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clearAuth() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var clearTableMsg = "TRUNCATE TABLE authData";
            try (var clearTableStatement = conn.prepareStatement(clearTableMsg)) {
                clearTableStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}