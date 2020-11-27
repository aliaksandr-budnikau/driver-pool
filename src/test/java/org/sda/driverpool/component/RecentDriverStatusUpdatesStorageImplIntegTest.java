package org.sda.driverpool.component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.sda.driverpool.entity.DriverStatus.ON_RIDE;
import static org.sda.driverpool.entity.DriverStatus.PENDING_ORDER;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 10, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestPropertySource("classpath:test.properties")
public class RecentDriverStatusUpdatesStorageImplIntegTest {

    @Autowired
    private RecentDriverStatusUpdatesStorage storage;

    @Test
    public void getAll_simpleTest10Added10Extracted() throws NothingFoundException {
        asList(ON_RIDE, PENDING_ORDER).forEach(status -> {
            for (int i = 0; i < 4; i++) {
                RecentDriverStatusUpdate update = new RecentDriverStatusUpdate("driver" + i, 23.0f, 23.0f, status, "event" + i);
                storage.add(update);
            }
        });
        assertTrue(storage.getAll(true).size() > 5);

        String driver1Id = "driver1";
        RecentDriverStatusUpdate driver1 = storage.getById(driver1Id);
        assertEquals(PENDING_ORDER, driver1.getStatus());
        assertEquals(driver1Id, driver1.getDriverId());


        RecentDriverStatusUpdate newEvent1 = new RecentDriverStatusUpdate("driver2", 23.0f, 23.0f, ON_RIDE, "newEvent1");
        storage.add(newEvent1);
        RecentDriverStatusUpdate newEvent2 = new RecentDriverStatusUpdate("driver2", 23.0f, 23.0f, PENDING_ORDER, "newEvent2");
        storage.add(newEvent2);

        String driver2Id = "driver2";
        List<RecentDriverStatusUpdate> drivers = storage.getById(driver2Id, 2);
        assertEquals(PENDING_ORDER, drivers.get(0).getStatus());
        assertEquals(ON_RIDE, drivers.get(1).getStatus());
        assertEquals(driver2Id, drivers.get(0).getDriverId());
        assertEquals(driver2Id, drivers.get(1).getDriverId());
        assertEquals("newEvent2", drivers.get(0).getEventId());
        assertEquals("newEvent1", drivers.get(1).getEventId());
    }
}