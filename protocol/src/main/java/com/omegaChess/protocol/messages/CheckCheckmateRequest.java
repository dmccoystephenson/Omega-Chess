// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class CheckCheckmateRequest {

    private final int id;

    public CheckCheckmateRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
