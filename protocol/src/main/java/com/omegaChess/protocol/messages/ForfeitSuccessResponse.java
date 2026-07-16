// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class ForfeitSuccessResponse {

    private final boolean success;
    private final boolean forfeit;

    public ForfeitSuccessResponse(boolean success, boolean forfeit) {
        this.success = success;
        this.forfeit = forfeit;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isForfeit() {
        return forfeit;
    }
}
