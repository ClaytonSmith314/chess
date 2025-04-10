package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    /**
     * @return a bool indicating whether the position is inside the chessboard or not
     */
    public boolean inBounds() {
        return row >= 1 && col >= 1
                && row <= 8 && col <= 8;
    }

    /**
     * @return a new ChessPosition with row shifted by dx and col shifted by dy
     */
    public ChessPosition shifted(int dx, int dy) {
        return new ChessPosition(row + dx, col + dy);
    }

    private static final String colVals = "abcdefgh";
    @Override
    public String toString() {

        return colVals.substring(col-1,col)+row;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ChessPosition other) {
            return row == other.getRow() && col == other.getColumn();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 8*row + col;
    }
}
