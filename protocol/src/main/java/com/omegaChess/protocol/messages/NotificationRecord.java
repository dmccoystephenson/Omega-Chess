// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class NotificationRecord {

    private final String event;
    private final String message;
    private final String dateString;

    public NotificationRecord(String event, String message, String dateString) {
        this.event = event;
        this.message = message;
        this.dateString = dateString;
    }

    public String getEvent() {
        return event;
    }

    public String getMessage() {
        return message;
    }

    public String getDateString() {
        return dateString;
    }
}
