// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class SquareRequest {

    private final int number;

    public SquareRequest(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
