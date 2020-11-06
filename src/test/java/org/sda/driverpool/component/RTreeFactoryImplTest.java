package org.sda.driverpool.component;

import org.junit.Test;
import org.sda.driverpool.entity.DriverStatus;
import org.sda.driverpool.entity.RTreeIndex;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;

import java.util.HashSet;
import java.util.List;

import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RTreeFactoryImplTest {
    private final float ORDER_LATITUDE = 53.897920f;
    private final float ORDER_LONGITUDE = 27.562034f;
    private final double ORDER_MAX_DISTANCE = 0.003854;
    private final int DRIVERS_NUMBER = 10;

    private final RTreeFactory factory = new RTreeFactoryImpl();

    @Test
    public void create_emptySetDrivers() {
        RTreeIndex index = factory.create(emptySet());
        assertTrue(index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, DRIVERS_NUMBER).isEmpty());
    }

    @Test
    public void create_onlyOneDriverAtExactlyTheSameSpotOfOrder() {
        RecentDriverStatusUpdate driver = new RecentDriverStatusUpdate("driver_id", ORDER_LATITUDE, ORDER_LONGITUDE, DriverStatus.PENDING_ORDER, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver);

        RTreeIndex index = factory.create(drivers);
        assertEquals(1, index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, DRIVERS_NUMBER).size());
    }

    @Test
    public void create_onlyOneDriverAtSomeSpotInRadius() {
        RecentDriverStatusUpdate driver = new RecentDriverStatusUpdate("driver_id", 53.899559f, 27.561097f, DriverStatus.PENDING_ORDER, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver);

        RTreeIndex index = factory.create(drivers);
        assertEquals(1, index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, DRIVERS_NUMBER).size());
    }

    @Test
    public void create_onlyOneDriverAtSomeSpotNotInRadius() {
        RecentDriverStatusUpdate driver = new RecentDriverStatusUpdate("driver_id", 53.901318f, 27.558033f, DriverStatus.PENDING_ORDER, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver);

        RTreeIndex index = factory.create(drivers);
        assertTrue(index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, DRIVERS_NUMBER).isEmpty());
    }

    @Test
    public void create_twoDriversAtExactlyTheSameSpotOfOrder() {
        RecentDriverStatusUpdate driver1 = new RecentDriverStatusUpdate("driver_id_1", ORDER_LATITUDE, ORDER_LONGITUDE, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver2 = new RecentDriverStatusUpdate("driver_id_2", ORDER_LATITUDE, ORDER_LONGITUDE, DriverStatus.PENDING_ORDER, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver1);
        drivers.add(driver2);

        RTreeIndex index = factory.create(drivers);
        assertEquals(2, index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, DRIVERS_NUMBER).size());
    }

    @Test
    public void create_twoDriversAtSomeSpotInRadius() {
        RecentDriverStatusUpdate driver1 = new RecentDriverStatusUpdate("driver_id_1", 53.899559f, 27.561097f, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver2 = new RecentDriverStatusUpdate("driver_id_2", 53.898984f, 27.564002f, DriverStatus.PENDING_ORDER, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver1);
        drivers.add(driver2);

        RTreeIndex index = factory.create(drivers);
        assertEquals(2, index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, DRIVERS_NUMBER).size());
    }

    @Test
    public void create_twoDriversAtSomeSpotNotInRadius() {
        RecentDriverStatusUpdate driver1 = new RecentDriverStatusUpdate("driver_id_1", 53.901318f, 27.558033f, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver2 = new RecentDriverStatusUpdate("driver_id_2", 53.900695f, 27.567624f, DriverStatus.PENDING_ORDER, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver1);
        drivers.add(driver2);

        RTreeIndex index = factory.create(drivers);
        assertTrue(index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, DRIVERS_NUMBER).isEmpty());
    }

    @Test
    public void create_twoDriversAtExactlyTheSameSpotOfOrder_withLimit() {
        RecentDriverStatusUpdate driver1 = new RecentDriverStatusUpdate("driver_id_1", ORDER_LATITUDE, ORDER_LONGITUDE, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver2 = new RecentDriverStatusUpdate("driver_id_2", ORDER_LATITUDE, ORDER_LONGITUDE, DriverStatus.PENDING_ORDER, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver1);
        drivers.add(driver2);

        RTreeIndex index = factory.create(drivers);
        assertEquals(1, index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, 1).size());
    }

    @Test
    public void create_twoDriversAtSomeSpotInRadius_withLimit() {
        RecentDriverStatusUpdate driver1 = new RecentDriverStatusUpdate("driver_id_1", 53.899559f, 27.561097f, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver2 = new RecentDriverStatusUpdate("driver_id_2", 53.898984f, 27.564002f, DriverStatus.PENDING_ORDER, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver1);
        drivers.add(driver2);

        RTreeIndex index = factory.create(drivers);
        assertEquals(1, index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, 1).size());
    }

    @Test
    public void create_twoDriversAtSomeSpotNotInRadius_withLimit() {
        RecentDriverStatusUpdate driver1 = new RecentDriverStatusUpdate("driver_id_1", 53.901318f, 27.558033f, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver2 = new RecentDriverStatusUpdate("driver_id_2", 53.900695f, 27.567624f, DriverStatus.PENDING_ORDER, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver1);
        drivers.add(driver2);

        RTreeIndex index = factory.create(drivers);
        assertTrue(index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, 1).isEmpty());
    }

    @Test
    public void create_sortingTest() {
        RecentDriverStatusUpdate driver1 = new RecentDriverStatusUpdate("driver_id_1", 53.897947f, 27.562278f, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver2 = new RecentDriverStatusUpdate("driver_id_2", 53.898268f, 27.562425f, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver3 = new RecentDriverStatusUpdate("driver_id_3", 53.898492f, 27.562919f, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver4 = new RecentDriverStatusUpdate("driver_id_4", 53.898796f, 27.563607f, DriverStatus.PENDING_ORDER, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver4);
        drivers.add(driver2);
        drivers.add(driver1);
        drivers.add(driver3);

        RTreeIndex index = factory.create(drivers);
        List<RecentDriverStatusUpdate> result = index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, 10);
        assertEquals(driver1, result.get(0));
        assertEquals(driver2, result.get(1));
        assertEquals(driver3, result.get(2));
        assertEquals(driver4, result.get(3));
        assertEquals(4, result.size());
    }


    @Test
    public void create_sorting_withStatuses() {
        RecentDriverStatusUpdate driver1 = new RecentDriverStatusUpdate("driver_id_1", 53.897947f, 27.562278f, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver2 = new RecentDriverStatusUpdate("driver_id_2", 53.898268f, 27.562425f, DriverStatus.ON_RIDE, "qweqwe");
        RecentDriverStatusUpdate driver3 = new RecentDriverStatusUpdate("driver_id_3", 53.898492f, 27.562919f, DriverStatus.PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate driver4 = new RecentDriverStatusUpdate("driver_id_4", 53.898796f, 27.563607f, DriverStatus.ON_RIDE, "qweqwe");

        HashSet<RecentDriverStatusUpdate> drivers = new HashSet<>();
        drivers.add(driver4);
        drivers.add(driver2);
        drivers.add(driver1);
        drivers.add(driver3);

        RTreeIndex index = factory.create(drivers);
        List<RecentDriverStatusUpdate> result = index.getClosest(ORDER_LATITUDE, ORDER_LONGITUDE, ORDER_MAX_DISTANCE, 10);
        assertEquals(driver1, result.get(0));
        assertEquals(driver3, result.get(1));
        assertEquals(2, result.size());
    }
}