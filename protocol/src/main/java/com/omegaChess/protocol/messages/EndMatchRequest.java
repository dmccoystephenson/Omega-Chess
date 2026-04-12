// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class EndMatchRequest {

    private final int id;
    private final String winner;
    private final String loser;

    public EndMatchRequest(int id, String winner, String loser) {
        this.id = id;
        this.winner = winner;
        this.loser = loser;
    }

    public int getId() {
        return id;
    }

    public String getWinner() {
        return winner;
    }

    public String getLoser() {
        return loser;
    }
}
