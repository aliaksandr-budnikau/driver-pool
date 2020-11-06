package org.sda.driverpool.event;

public class OrderGotDriverEvent {
    private final String driverId;

    public OrderGotDriverEvent(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverId() {
        return driverId;
    }
}
