// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class SquareSuccessResponse {

    private final boolean success;
    private final String answer;

    public SquareSuccessResponse(String answer) {
        this.success = true;
        this.answer = answer;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getAnswer() {
        return answer;
    }
}
