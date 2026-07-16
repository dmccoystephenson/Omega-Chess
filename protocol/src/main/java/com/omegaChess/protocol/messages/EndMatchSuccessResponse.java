// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class EndMatchSuccessResponse {

    private final boolean success;
    private final String archiveID;

    public EndMatchSuccessResponse(boolean success, String archiveID) {
        this.success = success;
        this.archiveID = archiveID;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getArchiveID() {
        return archiveID;
    }
}
