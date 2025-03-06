package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import service.Service;

import model.*;


public class Handler {

    private Gson serializer = new Gson();
    private Service service = new Service();


    public String handleClear(Request req, Response res){
        try {
            service.clear();
            res.status(200);
            return "{}";
        } catch (DataAccessException dataAccessException) {
            return serializeException(res, dataAccessException);
        }
    }

    public String handleRegister(Request req, Response res){
        try {
            var userData = serializer.fromJson(req.body(), UserData.class);
            var authData = service.register(userData);
            res.status(200);
            return serializer.toJson(authData);
        } catch (DataAccessException dataAccessException) {
            return serializeException(res, dataAccessException);
        }
    }

    public String handleLogin(Request req, Response res){
        try {
            var loginData = serializer.fromJson(req.body(), LoginData.class);
            var authData = service.login(loginData);
            res.status(200);
            return serializer.toJson(authData);
        } catch (DataAccessException dataAccessException) {
            return serializeException(res, dataAccessException);
        }
    }

    public String handleLogout(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            service.logout(authToken);
            res.status(200);
            return "{}";
        } catch (DataAccessException dataAccessException) {
            return serializeException(res, dataAccessException);
        }
    }

    public String handleListGames(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            var gameDataCollection = service.listGames(authToken);
            res.status(200);
            return serializer.toJson(gameDataCollection);
        } catch (DataAccessException dataAccessException) {
            return serializeException(res, dataAccessException);
        }
    }

    public String handleCreateGame(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            var gameName = serializer.fromJson(req.body(), GameName.class);
            var gameDataCollection = service.createGame(authToken, gameName);
            return serializer.toJson(gameDataCollection);
        } catch (DataAccessException dataAccessException) {
            return serializeException(res, dataAccessException);
        }
    }

    public String handleJoinGame(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            var joinGameData = serializer.fromJson(req.body(), JoinGameData.class);
            service.joinGame(authToken, joinGameData);
            return "{}";
        } catch (DataAccessException dataAccessException) {
            return serializeException(res, dataAccessException);
        }
    }

    private String serializeException(Response res, DataAccessException dataAccessException) {
        if(dataAccessException.getMessage().equals("Error: bad request"))
            res.status(400);
        else if(dataAccessException.getMessage().equals("Error: unauthorized"))
            res.status(401);
        else if(dataAccessException.getMessage().equals("Error: already taken"))
            res.status(403);
        else
            res.status(500);
        res.body(serializer.toJson(new FailureResponse(dataAccessException.getMessage())));
        return serializer.toJson(new FailureResponse(dataAccessException.getMessage()));
    }

}
