// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InProgressMatchesSuccessResponse {

    private final boolean success;
    private final int count;
    private final List<MatchSummary> matches;

    public InProgressMatchesSuccessResponse(boolean success, int count, List<MatchSummary> matches) {
        this.success = success;
        this.count = count;
        this.matches = Collections.unmodifiableList(new ArrayList<MatchSummary>(matches));
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCount() {
        return count;
    }

    public List<MatchSummary> getMatches() {
        return matches;
    }
}
