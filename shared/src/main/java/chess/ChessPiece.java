package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    private static int[][] diagonals = {{1,1},{1,-1},{-1,1},{-1,-1}};
    private static int[][] straghts = {{1,0},{0,1},{0,-1},{-1,0}};

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        switch (type) {
            case KING:
                break;
            case QUEEN:
                break;
            case BISHOP:
                break;
            case KNIGHT:
                break;
            case ROOK:
                break;
            case PAWN:
                break;
            default:
                System.out.println("Invalid piece type: "+type);
                break;
        }
        return null;
    }

    private void add_list_moves(Collection<ChessMove> moves, ChessBoard board,
                                ChessPosition myPosition, int[][] move_differences) {
        for(int i=0; i<move_differences.length; i++){
            ChessPosition targetPosition = myPosition.shifted(move_differences[i][0], move_differences[i][1]);
            if (targetPosition.inBounds()) {
                ChessPiece otherPiece = board.getPiece(targetPosition);
                if (otherPiece==null || otherPiece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, targetPosition, type));
                }
            }
        }
    }
    private void add_line_moves(Collection<ChessMove> pieceMoves, ChessBoard board,
                                ChessPosition position, int[][] move_deltas) {
        return;
    }
}
