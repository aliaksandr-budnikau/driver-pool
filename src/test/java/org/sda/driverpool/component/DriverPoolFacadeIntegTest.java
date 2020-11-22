package org.sda.driverpool.component;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sda.driverpool.entity.DriverStatus;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.sda.driverpool.event.OrderGotDriverEvent;
import org.sda.driverpool.event.OrderPendingDriverEvent;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DriverPoolFacadeIntegTest {

    private EventSender eventSender;
    private RecentDriverStatusUpdatesStorage storage;
    private DriverPoolFacade facade;

    @Before
    public void setUp() {
        eventSender = mock(EventSender.class);
        KafkaTemplate<String, RecentDriverStatusUpdate> template = mock(KafkaTemplate.class);
        KafkaConsumer<String, RecentDriverStatusUpdate> consumer = mock(KafkaConsumer.class);
        storage = new RecentDriverStatusUpdatesStorageImpl(consumer, template);

        BlockingRTreeHolder blockingRTreeHolder = new BlockingRTreeHolder();

        RTreeProvider rTreeProvider = new RTreeProviderImpl(blockingRTreeHolder);
        CurrentNodeMetaDataProviderImpl currentNodeMetaDataProvider = new CurrentNodeMetaDataProviderImpl();
        currentNodeMetaDataProvider.init();
        RecentDriverStatusUpdateFactoryImpl recentDriverStatusUpdateFactory = new RecentDriverStatusUpdateFactoryImpl(currentNodeMetaDataProvider);
        facade = new DriverPoolFacade(rTreeProvider, storage, eventSender, new BookingServiceImpl(storage, recentDriverStatusUpdateFactory), recentDriverStatusUpdateFactory);
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

        new RTreeRefresher(new RTreeFactoryImpl(), blockingRTreeHolder, storage).refresh();
    }

    @Test
    public void test() throws InterruptedException {
        OrderPendingDriverEvent event = new OrderPendingDriverEvent(53.897920f, 27.562034f, 0.003854);
        facade.handle(event);

        ArgumentCaptor<OrderGotDriverEvent> captor = ArgumentCaptor.forClass(OrderGotDriverEvent.class);
        verify(eventSender).send(captor.capture());
        OrderGotDriverEvent outEvent = captor.getValue();
        assertEquals("driver102", outEvent.getDriverId());
    }
}
