// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardDataSuccessResponse {

    private final boolean success;
    private final List<PieceEntry> pieces;

    public BoardDataSuccessResponse(boolean success, List<PieceEntry> pieces) {
        this.success = success;
        this.pieces = Collections.unmodifiableList(new ArrayList<PieceEntry>(pieces));
    }

    public boolean isSuccess() {
        return success;
    }

    public List<PieceEntry> getPieces() {
        return pieces;
    }
}
