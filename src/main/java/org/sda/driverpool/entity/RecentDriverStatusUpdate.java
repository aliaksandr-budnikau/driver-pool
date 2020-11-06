package org.sda.driverpool.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RecentDriverStatusUpdate {
    @EqualsAndHashCode.Include
    private final String driverId;
    private final float latitude;
    private final float longitude;
    @EqualsAndHashCode.Include
    private final DriverStatus status;
    @EqualsAndHashCode.Include
    private final String eventSourceNodeId;
}
