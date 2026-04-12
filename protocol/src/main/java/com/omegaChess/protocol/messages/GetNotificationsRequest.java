// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class GetNotificationsRequest {

    private final String nickname;

    public GetNotificationsRequest(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
