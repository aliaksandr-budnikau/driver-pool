package org.sda.driverpool.component;

import lombok.AllArgsConstructor;
import org.sda.driverpool.entity.DriverStatus;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RecentDriverStatusUpdateFactoryImpl implements RecentDriverStatusUpdateFactory {

    private final CurrentNodeMetaDataProvider currentNodeMetaDataProvider;

    @Override
    public RecentDriverStatusUpdate get(String driverId, float latitude, float longitude, DriverStatus status) {
        String currentNodeId = currentNodeMetaDataProvider.getCurrentNodeId();
        return new RecentDriverStatusUpdate(driverId, latitude, longitude, status, currentNodeId);
    }
}