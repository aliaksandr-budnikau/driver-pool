package org.sda.driverpool.dto;

import lombok.Data;

@Data
public class RecentDriverStatusUpdateDTO {
    private String driverId;
    private float latitude;
    private float longitude;
    private String status;
    private String eventId;
}
