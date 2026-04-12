// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class SendInviteRequest {

    private final String inviter;
    private final String invitee;

    public SendInviteRequest(String inviter, String invitee) {
        this.inviter = inviter;
        this.invitee = invitee;
    }

    public String getInviter() {
        return inviter;
    }

    public String getInvitee() {
        return invitee;
    }
}
