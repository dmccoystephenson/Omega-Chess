// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class GetGameRecordsRequest {

    private final String user;

    public GetGameRecordsRequest(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
