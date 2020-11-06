package org.sda.driverpool.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class OrderPendingDriverEvent {
    private final float latitude;
    private final float longitude;
    private final double maxDistance;
}
