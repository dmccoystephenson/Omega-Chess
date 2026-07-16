// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class TurnSuccessResponse {

    private final boolean success;
    private final String user;
    private final String color;

    public TurnSuccessResponse(boolean success, String user, String color) {
        this.success = success;
        this.user = user;
        this.color = color;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUser() {
        return user;
    }

    public String getColor() {
        return color;
    }
}
