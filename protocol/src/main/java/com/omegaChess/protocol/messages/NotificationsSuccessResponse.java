// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsSuccessResponse {

    private final boolean success;
    private final int count;
    private final List<NotificationRecord> notifications;

    public NotificationsSuccessResponse(boolean success, int count, List<NotificationRecord> notifications) {
        this.success = success;
        this.count = count;
        this.notifications = Collections.unmodifiableList(new ArrayList<NotificationRecord>(notifications));
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCount() {
        return count;
    }

    public List<NotificationRecord> getNotifications() {
        return notifications;
    }
}
