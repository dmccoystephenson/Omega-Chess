// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class MatchSummary {

    private final String opponentNickname;
    private final int matchID;
    private final int playerIndex;

    public MatchSummary(String opponentNickname, int matchID, int playerIndex) {
        this.opponentNickname = opponentNickname;
        this.matchID = matchID;
        this.playerIndex = playerIndex;
    }

    public String getOpponentNickname() {
        return opponentNickname;
    }

    public int getMatchID() {
        return matchID;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }
}
