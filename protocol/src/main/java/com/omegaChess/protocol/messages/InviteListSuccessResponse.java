// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InviteListSuccessResponse {

    private final boolean success;
    private final int amount;
    private final int totalCount;
    private final int maxNicknameLength;
    private final List<InviteRecord> invites;

    public InviteListSuccessResponse(boolean success, int amount, int totalCount, int maxNicknameLength, List<InviteRecord> invites) {
        this.success = success;
        this.amount = amount;
        this.totalCount = totalCount;
        this.maxNicknameLength = maxNicknameLength;
        this.invites = Collections.unmodifiableList(new ArrayList<InviteRecord>(invites));
    }

    public boolean isSuccess() {
        return success;
    }

    public int getAmount() {
        return amount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getMaxNicknameLength() {
        return maxNicknameLength;
    }

    public List<InviteRecord> getInvites() {
        return invites;
    }
}
