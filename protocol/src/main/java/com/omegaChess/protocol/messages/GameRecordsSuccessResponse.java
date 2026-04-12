// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRecordsSuccessResponse {

    private final boolean success;
    private final int number;
    private final List<GameRecordEntry> records;

    public GameRecordsSuccessResponse(boolean success, int number, List<GameRecordEntry> records) {
        this.success = success;
        this.number = number;
        this.records = Collections.unmodifiableList(new ArrayList<GameRecordEntry>(records));
    }

    public boolean isSuccess() {
        return success;
    }

    public int getNumber() {
        return number;
    }

    public List<GameRecordEntry> getRecords() {
        return records;
    }
}
