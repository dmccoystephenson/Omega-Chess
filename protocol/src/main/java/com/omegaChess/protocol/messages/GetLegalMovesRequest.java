// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class GetLegalMovesRequest {

    private final int matchID;
    private final int row;
    private final int column;

    public GetLegalMovesRequest(int matchID, int row, int column) {
        this.matchID = matchID;
        this.row = row;
        this.column = column;
    }

    public int getMatchID() {
        return matchID;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
