package ui;

import client.HttpException;
import client.ServerFacade;
import model.*;

import java.util.Collection;
import java.util.Scanner;

public class ChessUI {

    private boolean loggedIn = false;
    private AuthData sessionAuthData = null;
    private ServerFacade serverFacade;

    public ChessUI(int port) {
        serverFacade = new ServerFacade(port);
    }


    public boolean executePrompt() {
        if (loggedIn) {
            System.out.print("[LOGGED_IN] >>> ");
        } else {
            System.out.print("[LOGGED_OUT] >>> ");
        }
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] args = line.split(" ");


        try {
            if(loggedIn) {
                if(args[0].equals("help")) {
                    helpLoggedIn();
                } else if (args[0].equals("logout")) {
                    logout();
                } else if (args[0].equals("quit")) {
                    logout();
                    return false;
                } else if (args[0].equals("create")) {
                    createGame(args);
                } else if (args[0].equals("list")) {
                    listGames();
                } else {
                    handleBadCommand(args[0]);
                }
            } else {
                if(args[0].equals("help")) {
                    helpLoggedOut();
                } else if (args[0].equals("quit")) {
                    return false;
                } else if (args[0].equals("register")) {
                    register(args);
                } else if (args[0].equals("login")) {
                    login(args);
                } else if (args[0].equals("clear")) { //TODO: remove
                    clear();
                } else {
                    handleBadCommand(args[0]);
                }
            }
        } catch(HttpException e) {
            handleHttpException(e);
        }

        return true;
    }

    //TODO: remove
    private void clear() {
        serverFacade.requestClear();
    }

    private void helpLoggedOut() {
        System.out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - exit terminal session
                help - with possible commands
                """);
    }

    private void register(String[] args) {
        if(args.length!=4) {
            handleBadArgs(args[0]);
            return;
        }
        UserData userData = new UserData(args[1], args[2], args[3]);
        sessionAuthData = serverFacade.requestRegister(userData);
        loggedIn = true;
        System.out.println("Logged in as "+userData.username());
    }

    private void login(String[] args) {
        if(args.length!=3) {
            handleBadArgs(args[0]);
            return;
        }
        LoginData loginData = new LoginData(args[1], args[2]);
        sessionAuthData = serverFacade.requestLogin(loginData);
        loggedIn = true;
        System.out.println("Logged in as "+loginData.username());
    }

    private void helpLoggedIn() {
        System.out.println("""
                create <GAME NAME> - create a chess game
                list - list available games
                join <ID> [WHITE|BLACK] - join a game
                observe <ID> - observe a game
                logout - logout when you are done
                quit - playing chess
                help - with possible commands
                """);
    }

    private void logout() {
        serverFacade.requestLogout(sessionAuthData.authToken());
        sessionAuthData = null;
        loggedIn = false;
    }

    private void createGame(String[] args) {
        if(args.length!=2) {
            handleBadArgs(args[0]);
            return;
        }
        GameId gameId = serverFacade.requestCreateGame(
                sessionAuthData.authToken(), new GameName(args[1]));
        System.out.println("Created game "+args[1]+" with game id "+gameId.gameID());
    }

    private void listGames() {
        Collection<GameData> games = serverFacade.requestListGames(sessionAuthData.authToken());
        System.out.println("GAME ID\t|\tNAME");
        for(var game:games) {
            System.out.println(game.gameID()+"\t|\t"+game.gameName());
        }
        System.out.println();
    }

    private void handleBadCommand(String command) {
        System.out.println(command+" is not a command. Enter 'help' for available commands");
    }

    private void handleBadArgs(String command) {
        System.out.println("Incorrect arguments for command "+command+". Enter 'help' for command usage");
    }

    private void handleHttpException(HttpException e) {
        System.out.println(e.getMessage());
    }
}
