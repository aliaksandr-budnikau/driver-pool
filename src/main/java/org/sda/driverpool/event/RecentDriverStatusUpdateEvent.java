package org.sda.driverpool.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sda.driverpool.entity.DriverStatus;

@Getter
@AllArgsConstructor
public class RecentDriverStatusUpdateEvent {
    private final String driverId;
    private final float latitude;
    private final float longitude;
    private final DriverStatus status;
}
