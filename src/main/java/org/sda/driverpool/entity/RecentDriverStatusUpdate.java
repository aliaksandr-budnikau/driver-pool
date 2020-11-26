package org.sda.driverpool.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RecentDriverStatusUpdate {
    @EqualsAndHashCode.Include
    private String driverId;
    private float latitude;
    private float longitude;
    @EqualsAndHashCode.Include
    private DriverStatus status;
    private String eventId;
}
