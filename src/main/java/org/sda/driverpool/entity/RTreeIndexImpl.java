package org.sda.driverpool.entity;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.davidmoten.rtree.geometry.Geometries.point;

@Log4j2
@AllArgsConstructor
public class RTreeIndexImpl implements RTreeIndex {
    private final RTree<RecentDriverStatusUpdate, Point> rTree;

    @Override
    public int getSize() {
        return rTree.size();
    }

    @Override
    public List<RecentDriverStatusUpdate> getClosest(float latitude, float longitude, double maxDistance, int maxCount) {
        List<Entry<RecentDriverStatusUpdate, Point>> entries = rTree.nearest(point(latitude, longitude), maxDistance, maxCount)
                .filter(this::leaveOnlyInStatusPendingOrder)
                .toList().toBlocking().single();
        return entries.stream().map(Entry::value).collect(Collectors.toList());
    }

    private boolean leaveOnlyInStatusPendingOrder(Entry<RecentDriverStatusUpdate, Point> entry) {
        RecentDriverStatusUpdate value = entry.value();
        String driverId = value.getDriverId();
        DriverStatus status = value.getStatus();
        log.debug("Checking driverId {} with status {}", driverId, status);
        return status == DriverStatus.PENDING_ORDER;
    }
}