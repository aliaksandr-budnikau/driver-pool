package org.sda.driverpool.component;

import lombok.AllArgsConstructor;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Component
@AllArgsConstructor
class RecentDriverStatusUpdatesStorageImpl implements RecentDriverStatusUpdatesStorage {
    private final LinkedList<RecentDriverStatusUpdate> recentDriverStatusUpdatesList;

    @Override
    public Set<RecentDriverStatusUpdate> getAll() {
        return new HashSet<>(recentDriverStatusUpdatesList);
    }

    @Override
    public void add(RecentDriverStatusUpdate update) {
        recentDriverStatusUpdatesList.addFirst(update);
    }

    @Override
    public RecentDriverStatusUpdate getById(String driverId) throws NothingFoundException {
        return getById(driverId, 1)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NothingFoundException("Status for driver " + driverId + " wasn't found"));
    }

    @Override
    public List<RecentDriverStatusUpdate> getById(String driverId, int count) {
        return recentDriverStatusUpdatesList.stream()
                .filter(driver -> driverId.equals(driver.getDriverId()))
                .limit(count)
                .collect(toList());
    }
}
