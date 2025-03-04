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
            return "{}";
        } catch (DataAccessException dataAccessException) {
            return serializer.toJson(dataAccessException);
        }
    }

    public String handleRegister(Request req, Response res){
        try {
            var userData = serializer.fromJson(req.body(), UserData.class);
            var authData = service.register(userData);
            return serializer.toJson(authData);
        } catch (DataAccessException dataAccessException) {
            return serializer.toJson(dataAccessException);
        }
    }

    public String handleLogin(Request req, Response res){
        try {
            var loginData = serializer.fromJson(req.body(), LoginData.class);
            var authData = service.login(loginData);
            return serializer.toJson(authData);
        } catch (DataAccessException dataAccessException) {
            return serializer.toJson(dataAccessException);
        }
    }

    public String handleLogout(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            service.logout(authToken);
            return "{}";
        } catch (DataAccessException dataAccessException) {
            return serializer.toJson(dataAccessException);
        }
    }

    public String handleListGames(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            var gameDataCollection = service.listGames(authToken);
            return serializer.toJson(gameDataCollection);
        } catch (DataAccessException dataAccessException) {
            return serializer.toJson(dataAccessException);
        }
    }

    public String handleCreateGame(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            var gameName = serializer.fromJson(req.body(), GameName.class);
            var gameDataCollection = service.createGame(authToken, gameName);
            return serializer.toJson(gameDataCollection);
        } catch (DataAccessException dataAccessException) {
            return serializer.toJson(dataAccessException);
        }
    }

    public String handleJoinGame(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            var joinGameData = serializer.fromJson(req.body(), JoinGameData.class);
            service.joinGame(authToken, joinGameData);
            return "{}";
        } catch (DataAccessException dataAccessException) {
            return serializer.toJson(dataAccessException);
        }
    }

}
