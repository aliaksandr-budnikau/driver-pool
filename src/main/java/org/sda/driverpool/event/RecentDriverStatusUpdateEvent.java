package org.sda.driverpool.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sda.driverpool.entity.DriverStatus;

import static org.sda.driverpool.event.RecentDriverStatusUpdateEvent.EVENT_NAME;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(EVENT_NAME)
public class RecentDriverStatusUpdateEvent extends AbstractEvent {
    static final String EVENT_NAME = "recent_driver_status_update_event";
    private String driverId;
    private float latitude;
    private float longitude;
    private DriverStatus status;
}
