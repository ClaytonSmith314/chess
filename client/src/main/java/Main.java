import chess.*;
import ui.ChessUI;

public class Main {

    private static ChessUI chessUI;

    public static void main(String[] args) {
        chessUI = new ChessUI(8080);

        boolean hasNotQuit = true;
        while(hasNotQuit) {
            hasNotQuit = chessUI.executePrompt();
        };
    }
}