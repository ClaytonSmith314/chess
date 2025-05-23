package ui;

import chess.*;
import client.HttpException;
import client.ServerFacade;
import client.WSServerFacade;
import model.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class ChessUI {
    private static final String COL_LETTERS = "abcdefgh";
    private ServerFacade serverFacade;
    private final HashMap<Integer, Integer> gameIdMap = new HashMap<>();
    private boolean loggedIn = false;
    private AuthData sessionAuthData = null;
    private boolean gamePlayMode = false;
    private boolean isObserver = false;
    private boolean isBlackPlayer = false;
    private int gameId;
    private String gameName;
    private WSServerFacade wsServerFacade;
    private GameData gameData;

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
            if(gamePlayMode) {
                System.out.print("[IN_GAME] >>> ");
            } else {
                System.out.print("[LOGGED_IN] >>> ");
            }
        } else {
            System.out.print("[LOGGED_OUT] >>> ");
        }
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] args = line.split(" ");
        try {
            if(gamePlayMode) {
                if(args[0].equals("help")) {
                    helpInGamePlay();
                } else if (args[0].equals("leave")) {
                    leave();
                } else if (args[0].equals("redraw")) {
                    redraw();
                } else if (args[0].equals("move")) {
                    move(args);
                } else if(args[0].equals("resign")) {
                    resign();
                } else if(args[0].equals("showmoves")) {
                    showMoves(args);
                } else {
                    handleBadCommand(args[0]);
                }
            } else if(loggedIn) {
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
        } catch (Exception e) {
            handleGeneralException(e);
        }
        return true;
    }
    private void showMoves(String[] args) {
        if(args.length != 2) {
            handleBadArgs(args[0]);
        }
        ChessPosition position = parseChessPosition(args[1]);
        ChessPiece piece = gameData.game().getBoard().getPiece(position);
        if(piece==null) {
            System.out.println("Error: chess position "+position+" is empty");
            return;
        }
        Collection<ChessMove> moves = gameData.game().validMoves(position);
        printGameBoard(gameData.game().getBoard(), isBlackPlayer&&(!isObserver),
                        position, moves);
    }
    private void helpInGamePlay() {
        if(isObserver) {
            System.out.println("""
                    showmoves <ROW><COL> - show the legal moves of the piece at that location
                    redraw - redraw the chess board
                    leave - stop observing the game
                    help - with commands
                    """);
        } else {
            System.out.println("""
                    move <START POSITION> <END POSITION> - move a chess piece if it's your turn
                    showmoves <ROW><COL> - show the legal moves at the column
                    redraw - redraw the chess board
                    resign - forfeit the game
                    leave - stop observing the game
                    help - with commands
                    """);
        }
    }
    private void leave() throws Exception {
        gamePlayMode = false;
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                sessionAuthData.authToken(),
                gameId);
        wsServerFacade.send(command);
        wsServerFacade.close();
    }
    private void move(String[] args) throws Exception {
        var startPosition = parseChessPosition(args[1]);
        var endPosition = parseChessPosition(args[2]);
        if (startPosition==null || endPosition==null) {
            handleBadArgs("move");
            return;
        }
        ChessPiece piece = gameData.game().getBoard().getPiece(startPosition);
        ChessPiece.PieceType promotion = null;
        if(piece!=null && piece.getPieceType()==ChessPiece.PieceType.PAWN
            && (endPosition.getRow()==1 || endPosition.getRow()==8)) {
            if(args.length!=4) {
                System.out.println("Error: please specify the promotion piece (one of Q, R, B, Kn)");
                return;
            }
            switch(args[3].charAt(0)) {
                default -> {System.out.println("Error: incorrect promotion piece type (should be one of Q, R, B, Kn)");}
                case 'Q' -> {promotion = ChessPiece.PieceType.QUEEN;}
                case 'R' -> {promotion = ChessPiece.PieceType.ROOK;}
                case 'B' -> {promotion = ChessPiece.PieceType.BISHOP;}
                case 'K' -> {
                    if(args[3].length()<2 || args[3].charAt(1)!='n') {
                        System.out.println("Error: incorrect promotion piece type (should be one of Q, R, B, Kn)");
                    } else {
                        promotion = ChessPiece.PieceType.KNIGHT;
                    }
                }
            }
        }
        ChessMove move = new ChessMove(
                startPosition,
                endPosition,
                promotion
        );
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                sessionAuthData.authToken(),
                gameId
        );
        command.move = move;
        wsServerFacade.sendAndWait(command);
    }
    private void resign() throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                sessionAuthData.authToken(),
                gameId
        );
        wsServerFacade.sendAndWait(command);
    }
    private ChessPosition parseChessPosition(String s) {
        if(s.length()!=2 || COL_LETTERS.indexOf(s.charAt(0))==-1
            || !Character.isDigit(s.charAt(1)) || Integer.parseInt(s.substring(1,2))>8) {
            return null;
        }
        int col = COL_LETTERS.indexOf(s.charAt(0))+1;
        int row = Integer.parseInt(s.substring(1,2));
        return new ChessPosition(row, col);
    }
    private void redraw() {
        printGameBoard(gameData.game().getBoard(), isBlackPlayer&&(!isObserver));
    }
    public void handleServerNotificationOrError(ServerMessage serverMessage) {
        System.out.println((serverMessage.getServerMessageType()== ServerMessage.ServerMessageType.ERROR)?
                serverMessage.errorMessage: serverMessage.message);
    }
    public void handleLoadGame(ServerMessage serverMessage) {
        if(serverMessage.message != null) {
            System.out.println();
            System.out.print(serverMessage.message);
        }
        this.gameData = serverMessage.game;
        System.out.println("White Player: "+serverMessage.game.whiteUsername());
        System.out.println("Black Player: "+serverMessage.game.blackUsername());
        redraw();
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
        reloadGames();
        int visualId = addNewId(gameId.gameID());
        System.out.println("Created game "+args[1]+" with game id "+visualId);
    }
    private void reloadGames() {
        Collection<GameData> games = serverFacade.requestListGames(sessionAuthData.authToken());
        for(var game:games) {
            addNewId(game.gameID());
        }
    }
    private void listGames() {
        Collection<GameData> games = serverFacade.requestListGames(sessionAuthData.authToken());
        System.out.println("ID\t|  GAME NAME\t|  WHITE PLAYER\t|  BLACK PLAYER\t|\n" +
                "-----------------------------------------------------");
        for(var game:games) {
            int visualId = addNewId(game.gameID());
            String whiteUsername = (game.whiteUsername()==null)? "": game.whiteUsername();
            String blackUsername = (game.blackUsername()==null)? "": game.blackUsername();
            String gameName = game.gameName();
            if(game.game().isGameOver()) {
                gameName = gameName + SET_TEXT_ITALIC+" (finished)"+RESET_TEXT_ITALIC;
            }
            System.out.println(visualId+"\t|  "+lengthen(gameName,10)+"|  "
                +lengthen(whiteUsername, 10)+"|  "+lengthen(blackUsername,10)+"|");
        }
        System.out.println();
    }
    private void joinGame(String[] args) {
        reloadGames();
        if(args.length!=3) {
            handleBadArgs(args[0]);
            return;
        }
        int gameId = remapGameId(args[1]);
        if(gameId==-1){
            return;
        }
        var joinGameData = new JoinGameData(args[2], gameId);
        serverFacade.requestJoinGame(sessionAuthData.authToken(), joinGameData);
        GameData gameData = getGame(gameId);
        isBlackPlayer = sessionAuthData.username().equals(gameData.blackUsername());
        openWebSocketConnection(gameId, gameData.gameName());
        isObserver = false;
    }
    private void observeGame(String[] args) {
        reloadGames();
        if(args.length!=2) {
            handleBadArgs(args[0]);
            return;
        }
        int gameId = remapGameId(args[1]);
        if(gameId==-1){
            return;
        }
        GameData gameData = getGame(gameId);
        openWebSocketConnection(gameId, gameData.gameName());
        isObserver = true;
        isBlackPlayer = false;
    }
    private int remapGameId(String gameIdString) {
        int gameId = 0;
        try {
            if(gameIdMap.get(Integer.valueOf(gameIdString))==null) {
                System.out.println("Error: no game with that id");
                return -1;
            }
            gameId = gameIdMap.get(Integer.valueOf(gameIdString));
        } catch(NumberFormatException e) {
            handleBadArgs(gameIdString);
            return -1;
        }
        return gameId;
    }
    private void openWebSocketConnection(int gameId, String gameName) {
        try {
            wsServerFacade = new WSServerFacade(this);
            UserGameCommand command = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT,
                    sessionAuthData.authToken(),
                    gameId);
            wsServerFacade.sendAndWait(command);
            gamePlayMode = true;
            this.gameName = gameName;
            this.gameId = gameId;
        } catch (Exception e) {
            handleGeneralException(e);
        }
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
        if(!command.isEmpty()) {
            System.out.println("Error: " + command + " is not a command. Enter 'help' for available commands");
        }
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
    private void handleGeneralException(Exception e) {
        System.out.println(e.getMessage());
    }
    private void printGameBoard(ChessBoard board, boolean flip) {
        printGameBoard(board, flip, null, null);
    }
    private void printGameBoard(ChessBoard board, boolean flip,
                                ChessPosition startPosition, Collection<ChessMove> moves) {
        boolean isWhite = true;
        drawCols(flip);
        for(int i=1; i<=8; i++) {
            int row = flip? i : 9-i;
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.print(" "+row+"\u2003");
            for(int j=1; j<=8; j++) {
                int col = flip? 9-j : j;
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                System.out.print(isWhite? EscapeSequences.SET_BG_COLOR_DARK_GREY: EscapeSequences.SET_BG_COLOR_BLACK);
                if(startPosition!=null) {
                    ChessMove move = new ChessMove(startPosition, position, null);
                    if(moves.contains(move)) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    }
                    if(startPosition.equals(position)) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
                    }
                }
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
    }

    private void drawCols(boolean flip) {
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.EMPTY);
        for(int j=0; j<8; j++) {
            int col = flip? 7-j : j;
            System.out.print("\u2003"+ COL_LETTERS.charAt(col)+" ");
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