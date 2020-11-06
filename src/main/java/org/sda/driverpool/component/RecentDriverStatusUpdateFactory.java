package org.sda.driverpool.component;

import org.sda.driverpool.entity.DriverStatus;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;

public interface RecentDriverStatusUpdateFactory {
    RecentDriverStatusUpdate get(String driverId, float latitude, float longitude, DriverStatus status);
}