package handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;


public class Handler {

    private Gson serializer = new Gson();

    public String clear(Request req, Response res){
        return "";
    }

    public String register(Request req, Response res){
        return "";
    }

    public String login(Request req, Response res){
        return "";
    }

    public String logout(Request req, Response res){
        return "";
    }

    public String listGames(Request req, Response res){
        return "";
    }

    public String createGame(Request req, Response res){
        return "";
    }

    public String joinGame(Request req, Response res){
        return "";
    }

}
