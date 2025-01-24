package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] chess_board_array = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chess_board_array[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
            return chess_board_array[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for(int i=0; i<8; i++)
            for (int j = 0; j < 8; j++)
                chess_board_array[i][j] = null;
        for(int col=0; col<8; col++) {
            chess_board_array[1][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            chess_board_array[6][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        add_opposing_pieces(0, ChessPiece.PieceType.ROOK);
        add_opposing_pieces(1, ChessPiece.PieceType.KNIGHT);
        add_opposing_pieces(2, ChessPiece.PieceType.BISHOP);
        add_opposing_pieces(3, ChessPiece.PieceType.QUEEN);
        add_opposing_pieces(4, ChessPiece.PieceType.KING);
        add_opposing_pieces(5, ChessPiece.PieceType.BISHOP);
        add_opposing_pieces(6, ChessPiece.PieceType.KNIGHT);
        add_opposing_pieces(7, ChessPiece.PieceType.ROOK);
    }
    private void add_opposing_pieces(int col, ChessPiece.PieceType type) {
        chess_board_array[0][col] = new ChessPiece(ChessGame.TeamColor.WHITE, type);
        chess_board_array[7][col] = new ChessPiece(ChessGame.TeamColor.BLACK, type);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChessBoard other) {
            for(int i=1; i<=8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition position = new ChessPosition(i, j);
                    ChessPiece piece = getPiece(position);
                    ChessPiece otherPiece = other.getPiece(position);
                    if (piece==null && otherPiece!=null) return false;
                    if (piece!=null && otherPiece==null) return false;
                    if (piece!=null && !piece.equals(otherPiece)) return false;
                }
            }
            return true;
        } else return false;
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

}





