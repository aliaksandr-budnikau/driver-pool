package org.sda.driverpool.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@event")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderPendingDriverEvent.class, name = OrderPendingDriverEvent.EVENT_NAME),
        @JsonSubTypes.Type(value = RecentDriverStatusUpdateEvent.class, name = RecentDriverStatusUpdateEvent.EVENT_NAME)
})
public class AbstractEvent {
}