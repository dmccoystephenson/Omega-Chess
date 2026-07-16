// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class CheckForfeitRequest {

    private final int id;

    public CheckForfeitRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
