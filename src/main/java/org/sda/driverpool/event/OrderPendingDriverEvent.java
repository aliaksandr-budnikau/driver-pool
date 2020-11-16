package org.sda.driverpool.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderPendingDriverEvent extends AbstractEvent {
    private float latitude;
    private float longitude;
    private double maxDistance;
}
