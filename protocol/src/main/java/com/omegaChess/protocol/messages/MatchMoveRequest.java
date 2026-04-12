// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class MatchMoveRequest {

    private final int matchID;
    private final int fromRow;
    private final int fromColumn;
    private final int toRow;
    private final int toColumn;

    public MatchMoveRequest(int matchID, int fromRow, int fromColumn, int toRow, int toColumn) {
        this.matchID = matchID;
        this.fromRow = fromRow;
        this.fromColumn = fromColumn;
        this.toRow = toRow;
        this.toColumn = toColumn;
    }

    public int getMatchID() {
        return matchID;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromColumn() {
        return fromColumn;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToColumn() {
        return toColumn;
    }
}
