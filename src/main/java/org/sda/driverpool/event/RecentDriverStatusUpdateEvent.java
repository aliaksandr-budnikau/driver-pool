package org.sda.driverpool.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sda.driverpool.entity.DriverStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecentDriverStatusUpdateEvent extends AbstractEvent {
    private String driverId;
    private float latitude;
    private float longitude;
    private DriverStatus status;
}
