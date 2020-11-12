package org.sda.driverpool.component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.sda.driverpool.Application;
import org.sda.driverpool.event.OrderGotDriverEvent;
import org.sda.driverpool.event.OrderPendingDriverEvent;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.sda.driverpool.entity.DriverStatus.ON_RIDE;
import static org.sda.driverpool.entity.DriverStatus.PENDING_ORDER;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = Application.class, loader = AnnotationConfigContextLoader.class)
public class DriverPoolFacadeWithContextIntegTest {

    @MockBean
    private EventSender eventSender;
    @Autowired
    private DriverPoolFacade facade;
    @Autowired
    private RTreeRefresher refresher;

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        Thread driver1 = new Thread(() -> {
            await(countDownLatch);
            startLife(1, 53.898107f, 27.562054f);
        });
        driver1.start();
        countDownLatch.countDown();
        Thread driver2 = new Thread(() -> {
            await(countDownLatch);
            startLife(2, 53.898644f, 27.563259f);
        });
        driver2.start();
        countDownLatch.countDown();
        Thread driver3 = new Thread(() -> {
            await(countDownLatch);
            startLife(3, 53.899149f, 27.564343f);
        });
        driver3.start();
        countDownLatch.countDown();

        sleep(2000);
        refresher.refresh();

        OrderPendingDriverEvent event = new OrderPendingDriverEvent(53.897877f, 27.561506f, 0.003854);
        facade.handle(event);

        ArgumentCaptor<OrderGotDriverEvent> captor = ArgumentCaptor.forClass(OrderGotDriverEvent.class);
        verify(eventSender).send(captor.capture());
        OrderGotDriverEvent outEvent = captor.getValue();
        assertNotNull(outEvent);
    }

    private void startLife(int driverIndex, float latitude, float longitude) {
        Random random = new Random();
        int ridesCount = random.nextInt(10);
        for (int j = 0; j < ridesCount; j++) {
            int pendingOrderCount = random.nextInt(10);
            for (int i = 0; i < pendingOrderCount; i++) {
                facade.handle(new RecentDriverStatusUpdateEvent("driver" + driverIndex, latitude, longitude, PENDING_ORDER));
                sleep(random.nextInt(1000));
            }
            sleep(random.nextInt(1000));
            facade.handle(new RecentDriverStatusUpdateEvent("driver" + driverIndex, latitude, longitude, ON_RIDE));
        }
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            new RuntimeException(e);
        }
    }

    private void await(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            new RuntimeException(e);
        }
    }
}