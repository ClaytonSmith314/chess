package server;

import spark.*;

import handler.*;

public class Server {

    private Handler handler = new Handler();


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and clear exceptions here.

        Spark.delete("/db", (req,res)-> handler.handleClear(req,res));

        Spark.post("/user", (req,res)-> handler.handleRegister(req,res));

        Spark.post("/session", (req,res)->handler.handleLogin(req,res));
        Spark.delete("/session", (req,res)->handler.handleLogout(req,res));

        Spark.get("/game", (req,res)->handler.handleListGames(req,res));
        Spark.post("/game", (req,res)->handler.handleCreateGame(req,res));
        Spark.put("/game", (req,res)->handler.handleJoinGame(req,res));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
