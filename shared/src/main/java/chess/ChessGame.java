package chess;

import java.util.Collection;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    
    private TeamColor teamTurn;

    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;

    private Collection<ChessPosition> getEndPosition(Collection<ChessMove> moves) {
        Collection<ChessPosition> positions = new ArrayList<ChessPosition>();
        for(ChessMove move: moves) {
            positions.add(move.getEndPosition());
        }
        return positions;
    }

    private void findKings() {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (piece.getTeamColor() == TeamColor.WHITE) { whiteKingPosition = pos; }
                    else blackKingPosition = pos;
                }
            }
        }
    }


    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
        findKings();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if(piece==null) { return null; }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        ChessBoard boardSave = board.copy();
        for(ChessMove move: new ArrayList<ChessMove>(moves)) {
            ChessBoard saveBoard = board.copy();
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), piece);
            findKings();
            if (isInCheck(piece.getTeamColor())) { moves.remove(move); }
            board = saveBoard;
        }
        findKings();
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves!=null && validMoves.contains(move)) {
            ChessPiece piece = board.getPiece(move.getStartPosition());
            if(piece.getTeamColor()!=teamTurn) throw new InvalidMoveException();
            board.addPiece(move.getStartPosition(), null);
            ChessPiece.PieceType promotion = move.getPromotionPiece();
            if(promotion==null) {
                board.addPiece(move.getEndPosition(), piece);
            }
            else board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), promotion));
            findKings();
            teamTurn = (teamTurn==TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE;
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor otherColor = (teamColor==TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE;
        ChessPosition kingPosition = (teamColor==TeamColor.WHITE)? whiteKingPosition : blackKingPosition;
        for(int i=1; i<=8; i++) {
            for(int j=1; j<=8; j++) {
                ChessPosition pos = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(pos);
                if(piece!=null && piece.getTeamColor()==otherColor) {
                    if(getEndPosition(piece.pieceMoves(board, pos)).contains(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean kingTrapped(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(pos).isEmpty()) { return false; }
                }
            }
        }
        return true;
    }


        /**
         * Determines if the given team is in checkmate
         *
         * @param teamColor which team to check for checkmate
         * @return True if the specified team is in checkmate
         */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = (teamColor==TeamColor.WHITE)? whiteKingPosition : blackKingPosition;
        return validMoves(kingPosition).isEmpty() && isInCheck(teamColor) && kingTrapped(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) { return false; }
        return kingTrapped(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        findKings();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
