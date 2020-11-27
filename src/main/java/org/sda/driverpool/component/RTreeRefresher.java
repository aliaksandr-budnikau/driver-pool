package org.sda.driverpool.component;

import lombok.AllArgsConstructor;
import org.sda.driverpool.entity.RTreeIndex;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
class RTreeRefresher {
    private final static long REFRESH_RATE_IN_MILLISECONDS = 30_000;
    private final RTreeFactory rTreeFactory;
    private final BlockingRTreeHolder holder;
    private final RecentDriverStatusUpdatesStorage storage;

    @Scheduled(fixedDelay = REFRESH_RATE_IN_MILLISECONDS)
    public void refresh() {
        Set<RecentDriverStatusUpdate> drivers = storage.getAll(true);
        RTreeIndex rTree = rTreeFactory.create(drivers);
        holder.produce(rTree);
    }
}