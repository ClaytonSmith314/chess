package ui;

import model.AuthData;

import java.util.Scanner;

public class ChessUI {

    private boolean loggedIn = false;
    private AuthData sessionAuthData = null;

    public boolean executePrompt() {
        if (loggedIn) {
            System.out.print("[LOGGED_IN] >>> ");
        } else {
            System.out.print("[LOGGED_OUT] >>> ");
        }
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] args = line.split(" ");


        if(loggedIn) {
            if(args[0].equals("help")) {
                helpLoggedIn();
            } else if (args[0].equals("quit")) {

            } else {
                handleBadArgs(line);
            }
        } else {
            if(args[0].equals("help")) {
                helpLoggedOut();
            } else if (args[0].equals("quit")) {
                return false;
            } else {
                handleBadArgs(line);
            }
        }

        return true;
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

    private void helpLoggedOut() {
        System.out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - exit terminal session
                help - with possible commands
                """);
    }

    private void handleBadArgs(String line) {
        System.out.println(line+" is not a command. Enter 'help' for available commands");
    }
}
