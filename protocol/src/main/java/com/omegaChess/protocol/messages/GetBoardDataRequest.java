// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class GetBoardDataRequest {

    private final int id;

    public GetBoardDataRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
