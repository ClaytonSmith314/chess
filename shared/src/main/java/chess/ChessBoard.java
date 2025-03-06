package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] chessBoardArray = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessBoardArray[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
            return chessBoardArray[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for(int i=0; i<8; i++) {
            for (int j = 0; j < 8; j++) {
                chessBoardArray[i][j] = null;
            }
        }
        for(int col=0; col<8; col++) {
            chessBoardArray[1][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            chessBoardArray[6][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        addOpposingPieces(0, ChessPiece.PieceType.ROOK);
        addOpposingPieces(1, ChessPiece.PieceType.KNIGHT);
        addOpposingPieces(2, ChessPiece.PieceType.BISHOP);
        addOpposingPieces(3, ChessPiece.PieceType.QUEEN);
        addOpposingPieces(4, ChessPiece.PieceType.KING);
        addOpposingPieces(5, ChessPiece.PieceType.BISHOP);
        addOpposingPieces(6, ChessPiece.PieceType.KNIGHT);
        addOpposingPieces(7, ChessPiece.PieceType.ROOK);
    }
    private void addOpposingPieces(int col, ChessPiece.PieceType type) {
        chessBoardArray[0][col] = new ChessPiece(ChessGame.TeamColor.WHITE, type);
        chessBoardArray[7][col] = new ChessPiece(ChessGame.TeamColor.BLACK, type);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChessBoard other) {
            for(int i=1; i<=8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition position = new ChessPosition(i, j);
                    ChessPiece piece = getPiece(position);
                    ChessPiece otherPiece = other.getPiece(position);
                    if (piece==null && otherPiece!=null) { return false; }
                    if (piece!=null && otherPiece==null) { return false; }
                    if (piece!=null && !piece.equals(otherPiece)) { return false; }
                }
            }
            return true;
        } else {
            return false;
        }
    }
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int i=1; i<=8; i++) {
            s.append("\n");
            for(int j=1; j<=8; j++) {
                s.append(getPiece(new ChessPosition(i, j))).append("\t");
            }
        }
        return s.toString();
    }

    public ChessBoard copy() {
        ChessBoard other = new ChessBoard();
        for(int i=1; i<=8; i++) {
            for(int j=1; j<=8; j++) {
                ChessPosition pos = new ChessPosition(i,j);
                other.addPiece(pos, getPiece(pos));
            }
        }
        return other;
    }

}





