// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class CheckmateSuccessResponse {

    private final boolean success;
    private final boolean checkmate;
    private final String loser;
    private final String winner;

    public CheckmateSuccessResponse(boolean success, boolean checkmate, String loser, String winner) {
        this.success = success;
        this.checkmate = checkmate;
        this.loser = loser;
        this.winner = winner;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public String getLoser() {
        return loser;
    }

    public String getWinner() {
        return winner;
    }
}
