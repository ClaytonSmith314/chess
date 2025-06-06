package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;


    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public String toString() {
        return startPosition + "->" + endPosition;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ChessMove move) {

            if(startPosition.equals(move.getStartPosition())
                    && endPosition.equals(move.getEndPosition())) {
                if (promotionPiece == move.getPromotionPiece()) { return true; }
                else { return false; }
            } else { return false; }
        } else { return false; }
    }

    @Override
    public int hashCode() {
        if (promotionPiece == null) {
            return startPosition.hashCode() + 64*endPosition.hashCode();
        } else {
            return startPosition.hashCode() + 64*endPosition.hashCode() + 64*64*promotionPiece.hashCode();
        }
    }
}
