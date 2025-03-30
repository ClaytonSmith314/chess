package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.HttpException;
import client.ServerFacade;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

public class ChessUI {

    private boolean loggedIn = false;
    private AuthData sessionAuthData = null;
    private ServerFacade serverFacade;
    private final HashMap<Integer, Integer> gameIdMap = new HashMap<>();

    private int addNewId(int dbId) {
        if(!gameIdMap.containsValue(dbId)) {
            int visualId = gameIdMap.size()+1;
            gameIdMap.put(visualId, dbId);
            return visualId;
        } else {
            for(var key: gameIdMap.keySet()) {
                if (gameIdMap.get(key) == dbId) {
                    return key;
                }
            }
        }
        return -1;
    }



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
                } else {
                    handleBadCommand(args[0]);
                }
            }
        } catch(HttpException e) {
            handleHttpException(e);
        }

        return true;
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
        int visualId = addNewId(gameId.gameID());
        System.out.println("Created game "+args[1]+" with game id "+visualId);
    }

    private void listGames() {
        Collection<GameData> games = serverFacade.requestListGames(sessionAuthData.authToken());
        System.out.println("ID\t|  GAME NAME\t|  WHITE PLAYER\t|  BLACK PLAYER\t|\n" +
                "-----------------------------------------------------");
        for(var game:games) {
            int visualId = addNewId(game.gameID());
            String whiteUsername = (game.whiteUsername()==null)? "": game.whiteUsername();
            String blackUsername = (game.blackUsername()==null)? "": game.blackUsername();
            System.out.println(visualId+"\t|  "+lengthen(game.gameName(),10)+"|  "
                +lengthen(whiteUsername, 10)+"|  "+lengthen(blackUsername,10)+"|");
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
            gameId = gameIdMap.get(Integer.valueOf(args[1]));
        } catch(NumberFormatException e) {
            handleBadArgs(args[0]);
            return;
        }
        var joinGameData = new JoinGameData(args[2], gameId);
        serverFacade.requestJoinGame(sessionAuthData.authToken(), joinGameData);

        GameData gameData = getGame(gameId);

        System.out.println("Joined game "+gameData.gameName()+" with id "+gameId+" as team "+args[2]);

//        printGameBoard(gameData.game().getBoard(), args[2].equals("BLACK"));

        printGameBoard(gameData.game().getBoard(), false);
        printGameBoard(gameData.game().getBoard(), true);
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
        boolean flip = gameData.blackUsername()!=null && gameData.blackUsername().equals(sessionAuthData.username());
        printGameBoard(gameData.game().getBoard(), flip);
    }

    private GameData getGame(int visualGameId) {
        int serverGameId = gameIdMap.get(visualGameId);
        Collection<GameData> games = serverFacade.requestListGames(sessionAuthData.authToken());
        for(var game: games) {
            if(game.gameID()==serverGameId) {
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
        if(e.code == -1) {
            System.out.println("Error: internal error");
        } else {
            System.out.println(e.getMessage());
        }
    }



    private void printGameBoard(ChessBoard board, boolean flip) {
        boolean isWhite = true;
        drawCols(flip);
        for(int i=1; i<=8; i++) {
            int row = flip? i : 9-i;
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.print(" "+row+"\u2003");
            for(int j=1; j<=8; j++) {
                int col = flip? 9-j : j;
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                System.out.print(isWhite? EscapeSequences.SET_BG_COLOR_DARK_GREY: EscapeSequences.SET_BG_COLOR_BLACK);
                isWhite = !isWhite;
                System.out.print(drawPiece(piece));
            }
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.print(" "+row+"\u2003");
            System.out.println();
            isWhite = !isWhite;
        }
        drawCols(flip);
        System.out.println();
    }

    private void drawCols(boolean flip) {
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        String colLetters = "abcdefgh";
        System.out.print(EscapeSequences.EMPTY);
        for(int j=0; j<8; j++) {
            int col = flip? 7-j : j;
            System.out.print("\u2003"+colLetters.charAt(col)+" ");
        }
        System.out.println(EscapeSequences.EMPTY);
    }

    private String drawPiece(ChessPiece piece) {
        if(piece == null) {
            return EscapeSequences.EMPTY;
        } else if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            return switch (piece.getPieceType()) {
                case PAWN -> EscapeSequences.WHITE_PAWN;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case KING -> EscapeSequences.WHITE_KING;
            };
        } else {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_MAGENTA);
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

    private String lengthen(String src, int len) {
        StringBuilder out = new StringBuilder(src);
        while(out.length() < len) {
            out.append(" ");
        }
        out.append("\t");
        return out.toString();
    }

}
