package org.sda.driverpool.component;

import org.sda.driverpool.entity.RecentDriverStatusUpdate;

import java.util.List;
import java.util.Set;

interface RecentDriverStatusUpdatesStorage {
    Set<RecentDriverStatusUpdate> getAll(boolean fromCache);

    void add(RecentDriverStatusUpdate update);

    RecentDriverStatusUpdate getById(String driverId) throws NothingFoundException;

    List<RecentDriverStatusUpdate> getById(String driverId, int count);
}
