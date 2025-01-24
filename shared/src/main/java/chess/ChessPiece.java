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

    private static final int[][] diagonals = {{1,1},{1,-1},{-1,1},{-1,-1}};
    private static final int[][] straights = {{1,0},{0,1},{0,-1},{-1,0}};
    private static final int[][] knight_moves =
            {{2,1},{1,2},{-2,1},{1,-2},{2,-1},{-1,2},{-1,-2},{-2,-1}};

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public String toString() {
        return "";
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
                add_list_moves(moves, board, myPosition, diagonals);
                add_list_moves(moves, board, myPosition, straights);
                break;
            case QUEEN:
                add_line_moves(moves, board, myPosition, diagonals);
                add_line_moves(moves, board, myPosition, straights);
                break;
            case BISHOP:
                add_line_moves(moves, board, myPosition, diagonals);
                break;
            case KNIGHT:
                add_list_moves(moves, board, myPosition, knight_moves);
                break;
            case ROOK:
                add_line_moves(moves, board, myPosition, straights);
                break;
            case PAWN:
                add_pawn_moves(moves, board, myPosition);
                break;
            default:
                System.out.println("Invalid piece type: "+type);
                break;
        }
        return moves;
    }

    private void add_list_moves(Collection<ChessMove> moves, ChessBoard board,
                                ChessPosition myPosition, int[][] moveDifferences) {
        for (int[] moveDifference : moveDifferences) {
            ChessPosition targetPosition = myPosition.shifted(moveDifference[0], moveDifference[1]);
            if (targetPosition.inBounds()) {
                ChessPiece otherPiece = board.getPiece(targetPosition);
                if (otherPiece == null || otherPiece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, targetPosition, null));
                }
            }
        }
    }

    private void add_line_moves(Collection<ChessMove> moves, ChessBoard board,
                                ChessPosition myPosition, int[][] moveDeltas) {
        for (int[] moveDelta : moveDeltas) {
            for (int i=1; i<=8; i++) {
                ChessPosition targetPosition = myPosition.shifted(i*moveDelta[0], i*moveDelta[1]);
                if (!targetPosition.inBounds()) break;
                ChessPiece otherPiece = board.getPiece(targetPosition);
                if (otherPiece!=null && otherPiece.getTeamColor()==pieceColor) break;
                moves.add(new ChessMove(myPosition, targetPosition, null));
                if (otherPiece!=null && otherPiece.getTeamColor()!=pieceColor) break;
            }
        }
    }

    private void add_pawn_moves(Collection<ChessMove> moves,
                                ChessBoard board, ChessPosition myPosition) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE ? 1 : -1);
        ChessPosition targetPosition = myPosition.shifted(direction, 0);
        if (targetPosition.inBounds()) {
            ChessPiece otherPiece = board.getPiece(targetPosition);
            if (otherPiece == null) {
                add_pawn_move_with_promotion(moves, myPosition, targetPosition);
                if(myPosition.getRow()==4.5-direction*2.5) {
                    targetPosition = myPosition.shifted(2*direction, 0);
                    if(board.getPiece(targetPosition)==null)
                        moves.add(new ChessMove(myPosition, targetPosition, null));
                }
            }
        }
        for(int side=-1; side<=1; side+=2) {
            targetPosition = myPosition.shifted(direction, side);
            if (targetPosition.inBounds()) {
                ChessPiece otherPiece = board.getPiece(targetPosition);
                if (otherPiece != null && otherPiece.getTeamColor() != pieceColor) {
                    add_pawn_move_with_promotion(moves, myPosition, targetPosition);
                }
            }
        }
    }
    private void add_pawn_move_with_promotion(Collection<ChessMove> moves, ChessPosition myPosition,
                                              ChessPosition targetPosition) {
        if(targetPosition.getRow()==1 || targetPosition.getRow()==8) {
            for(PieceType pieceType : PieceType.values()) {
                if (pieceType==PieceType.PAWN || pieceType==PieceType.KING) continue;
                moves.add(new ChessMove(myPosition, targetPosition, pieceType));
            }
        } else moves.add(new ChessMove(myPosition, targetPosition, null));
    }
}











