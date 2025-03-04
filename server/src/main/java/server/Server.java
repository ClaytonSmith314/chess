package server;

import spark.*;

import handler.*;

public class Server {

    private Handler handler = new Handler();


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and clear exceptions here.

        Spark.delete("/db", (req,res)-> handler.clear(req,res));

        Spark.post("/user", (req,res)-> handler.register(req,res));

        Spark.post("/session", (req,res)->handler.login(req,res));
        Spark.delete("/session", (req,res)->handler.logout(req,res));

        Spark.get("/game", (req,res)->handler.listGames(req,res));
        Spark.post("/game", (req,res)->handler.createGame(req,res));
        Spark.put("/game", (req,res)->handler.joinGame(req,res));


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
