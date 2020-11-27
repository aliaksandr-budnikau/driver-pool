package org.sda.driverpool.component;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.sda.driverpool.entity.DriverStatus;
import org.sda.driverpool.event.OrderGotDriverEvent;
import org.sda.driverpool.event.OrderPendingDriverEvent;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 10, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestPropertySource("classpath:test.properties")
public class DriverPoolFacadeIntegTest {

    @Autowired
    private DriverPoolFacade facade;
    @MockBean
    private EventSender eventSender;
    @Autowired
    private RTreeRefresher refresher;

    @SneakyThrows
    @Before
    public void setUp() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            int driverId = random.nextInt(100);
            float position = driverId / 1000000f;
            facade.handle(new RecentDriverStatusUpdateEvent(
                            "driver" + driverId,
                            53.8984f + position,
                            27.5629f + position,
                            DriverStatus.values()[driverId % 2]
                    )
            );
        }
        facade.handle(new RecentDriverStatusUpdateEvent(
                        "driver101",
                        53.897883f,
                        27.562110f,
                        DriverStatus.PENDING_ORDER
                )
        );
        facade.handle(new RecentDriverStatusUpdateEvent(
                        "driver101",
                        53.897883f,
                        27.562110f,
                        DriverStatus.ON_RIDE
                )
        );
        facade.handle(new RecentDriverStatusUpdateEvent(
                        "driver102",
                        53.897940f,
                        27.562202f,
                        DriverStatus.PENDING_ORDER
                )
        );

        refresher.refresh();
    }

    @SneakyThrows
    @Test
    public void test() {
        facade.handle(new OrderPendingDriverEvent(53.897920f, 27.562034f, 0.003854));

        ArgumentCaptor<OrderGotDriverEvent> captor = ArgumentCaptor.forClass(OrderGotDriverEvent.class);
        verify(eventSender).send(captor.capture());
        OrderGotDriverEvent outEvent = captor.getValue();
        assertEquals("driver102", outEvent.getDriverId());
    }
}
