// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class SimpleSuccessResponse {

    private final boolean success;

    public SimpleSuccessResponse() {
        this.success = true;
    }

    public boolean isSuccess() {
        return success;
    }
}
