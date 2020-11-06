package org.sda.driverpool.component;

import org.sda.driverpool.entity.RecentDriverStatusUpdate;

interface BookingService {
    boolean tryBooking(RecentDriverStatusUpdate status);
}
