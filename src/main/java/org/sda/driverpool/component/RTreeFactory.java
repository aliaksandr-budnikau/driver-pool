package org.sda.driverpool.component;

import org.sda.driverpool.entity.RTreeIndex;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;

import java.util.Set;

interface RTreeFactory {
    RTreeIndex create(Set<RecentDriverStatusUpdate> drivers);
}