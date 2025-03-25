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

        if(args[0].equals("help")) {
            handleHelp();
        }
        return true;
    }

    private void handleHelp() {
        System.out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - exit terminal session
                help - with possible commands
                """);
    }
}
