package org.sda.driverpool.entity;

import java.util.List;

public interface RTreeIndex {
    int getSize();

    List<RecentDriverStatusUpdate> getClosest(float latitude, float longitude, double maxDistance, int maxCount);
}