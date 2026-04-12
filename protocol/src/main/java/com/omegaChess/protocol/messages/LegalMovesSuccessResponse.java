// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class LegalMovesSuccessResponse {

    private final boolean success;
    private final String legalMoves;
    private final boolean enPassant;

    public LegalMovesSuccessResponse(boolean success, String legalMoves, boolean enPassant) {
        this.success = success;
        this.legalMoves = legalMoves;
        this.enPassant = enPassant;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getLegalMoves() {
        return legalMoves;
    }

    public boolean isEnPassant() {
        return enPassant;
    }
}
