package org.sda.driverpool.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.sda.driverpool.event.OrderPendingDriverEvent.EVENT_NAME;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(EVENT_NAME)
public class OrderPendingDriverEvent extends AbstractEvent {
    static final String EVENT_NAME = "order_pending_driver_event";
    private float latitude;
    private float longitude;
    private double maxDistance;
}
