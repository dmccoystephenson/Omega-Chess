// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class InviteResponseRequest {

    private final String response;
    private final String inviter;
    private final String invitee;

    public InviteResponseRequest(String response, String inviter, String invitee) {
        this.response = response;
        this.inviter = inviter;
        this.invitee = invitee;
    }

    public String getResponse() {
        return response;
    }

    public String getInviter() {
        return inviter;
    }

    public String getInvitee() {
        return invitee;
    }
}
