package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
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
                } else if (args[0].equals("join")) {
                    joinGame(args);
                } else if (args[0].equals("observe")) {
                    observeGame(args);
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
        System.out.println("ID\t|  GAME NAME\n----------------");
        for(var game:games) {
            System.out.println(game.gameID()+"\t|  "+game.gameName());
        }
        System.out.println();
    }

    private void joinGame(String[] args) {
        if(args.length!=3) {
            handleBadArgs(args[0]);
            return;
        }
        int gameId = 0;
        try {
            gameId = Integer.valueOf(args[1]);
        } catch(NumberFormatException e) {
            handleBadArgs(args[0]);
            return;
        }
        var joinGameData = new JoinGameData(args[2], gameId);
        serverFacade.requestJoinGame(sessionAuthData.authToken(), joinGameData);

        GameData gameData = getGame(gameId);

        System.out.println("Joined game "+gameData.gameName()+" with id "+gameData.gameID()+" as team "+args[2]);

    }

    private void observeGame(String[] args) {
        if(args.length!=2) {
            handleBadArgs(args[0]);
            return;
        }
        int gameId = 0;
        try {
            gameId = Integer.valueOf(args[1]);
        } catch(NumberFormatException e) {
            handleBadArgs(args[0]);
            return;
        }
        GameData gameData = getGame(gameId);
        printGameBoard(gameData.game().getBoard(), false);
    }

    private GameData getGame(int gameId) {
        Collection<GameData> games = serverFacade.requestListGames(sessionAuthData.authToken());
        for(var game: games) {
            if(game.gameID()==gameId) {
                return game;
            }
        }
        return null;
    }

    private void handleBadCommand(String command) {
        System.out.println("Error: "+command+" is not a command. Enter 'help' for available commands");
    }

    private void handleBadArgs(String command) {
        System.out.println("Error: Incorrect arguments for command "+command+". Enter 'help' for command usage");
    }

    private void handleHttpException(HttpException e) {
        System.out.println(e.getMessage());
    }



    private void printGameBoard(ChessBoard board, boolean flip) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
        boolean isWhite = true;
        for(int i=1; i<=8; i++) {
            for(int j=1; j<=8; j++) {
                int row = flip? i : 9-i;
                ChessPiece piece = board.getPiece(new ChessPosition(row, j));
                System.out.print(isWhite? EscapeSequences.SET_BG_COLOR_WHITE: EscapeSequences.SET_BG_COLOR_BLACK);
                isWhite = !isWhite;
                System.out.print(drawPiece(piece));
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.println();
            isWhite = !isWhite;
        }
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
    }

    private String drawPiece(ChessPiece piece) {
        if(piece == null) {
            return EscapeSequences.EMPTY;
        } else if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return switch (piece.getPieceType()) {
                case PAWN -> EscapeSequences.WHITE_PAWN;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case KING -> EscapeSequences.WHITE_KING;
            };
        } else {
            return switch (piece.getPieceType()) {
                case PAWN -> EscapeSequences.BLACK_PAWN;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case KING -> EscapeSequences.BLACK_KING;
            };
        }
    }
}
