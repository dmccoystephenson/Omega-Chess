// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class GameRecordEntry {

    private final String opponentNickname;
    private final String result;
    private final int moves;

    public GameRecordEntry(String opponentNickname, String result, int moves) {
        this.opponentNickname = opponentNickname;
        this.result = result;
        this.moves = moves;
    }

    public String getOpponentNickname() {
        return opponentNickname;
    }

    public String getResult() {
        return result;
    }

    public int getMoves() {
        return moves;
    }
}
