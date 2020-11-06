package org.sda.driverpool.component;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import org.sda.driverpool.entity.RTreeIndex;
import org.sda.driverpool.entity.RTreeIndexImpl;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.github.davidmoten.rtree.geometry.Geometries.point;

@Component
class RTreeFactoryImpl implements RTreeFactory {
    @Override
    public RTreeIndex create(Set<RecentDriverStatusUpdate> drivers) {
        RTree<RecentDriverStatusUpdate, Point> tree = RTree.create();
        for (RecentDriverStatusUpdate driver : drivers) {
            tree = tree.add(driver, point(driver.getLatitude(), driver.getLongitude()));
        }
        return new RTreeIndexImpl(tree);
    }
}