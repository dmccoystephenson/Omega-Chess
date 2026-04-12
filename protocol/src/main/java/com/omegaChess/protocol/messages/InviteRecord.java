// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class InviteRecord {

    private final String inviter;
    private final String invitee;
    private final boolean accepted;
    private final boolean declined;

    public InviteRecord(String inviter, String invitee, boolean accepted, boolean declined) {
        this.inviter = inviter;
        this.invitee = invitee;
        this.accepted = accepted;
        this.declined = declined;
    }

    public String getInviter() {
        return inviter;
    }

    public String getInvitee() {
        return invitee;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isDeclined() {
        return declined;
    }
}
