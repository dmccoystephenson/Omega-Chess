// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class InviteResponseSuccessResponse {

    private final boolean success;
    private final String matchID;

    public InviteResponseSuccessResponse(boolean success, String matchID) {
        this.success = success;
        this.matchID = matchID;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMatchID() {
        return matchID;
    }
}
