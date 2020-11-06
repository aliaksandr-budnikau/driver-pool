package org.sda.driverpool.component;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.sda.driverpool.entity.DriverStatus;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.sda.driverpool.entity.DriverStatus.ON_RIDE;
import static org.sda.driverpool.entity.DriverStatus.PENDING_ORDER;

@Service
@AllArgsConstructor
@Log4j2
class BookingServiceImpl implements BookingService {

    private final RecentDriverStatusUpdatesStorage storage;
    private final RecentDriverStatusUpdateFactory recentDriverStatusUpdateFactory;
    private final CurrentNodeMetaDataProvider currentNodeMetaDataProvider;

    @Override
    public boolean tryBooking(RecentDriverStatusUpdate candidate) {
        log.info("Candidate {}", candidate);

        RecentDriverStatusUpdate actualDriverStatus;
        try {
            actualDriverStatus = storage.getById(candidate.getDriverId());
        } catch (NothingFoundException e) {
            log.warn("Skipping. Candidate's actual status wasn't found");
            return false;
        }

        log.info("Candidate actual status {}", actualDriverStatus);

        if (actualDriverStatus.getStatus() == ON_RIDE) {
            log.warn("Skipping. Candidate is already {}", ON_RIDE);
            return false;
        }

        if (actualDriverStatus.getStatus() == PENDING_ORDER) {
            RecentDriverStatusUpdate update = recentDriverStatusUpdateFactory.get(
                    actualDriverStatus.getDriverId(),
                    actualDriverStatus.getLatitude(),
                    actualDriverStatus.getLongitude(),
                    ON_RIDE
            );
            log.info("Preliminarily booking");
            storage.add(update);
            log.info("Booked");
        } else {
            log.warn("Skipping. Candidate has wrong actual status");
            return false;
        }

        List<RecentDriverStatusUpdate> lastTwo = storage.getById(candidate.getDriverId(), 2);
        if (lastTwo.size() != 2) {
            log.warn("Skipping. Insufficient amount of data. size = {}", lastTwo.size());
            return false;
        }

        DriverStatus newestStatus = lastTwo.get(0).getStatus();
        DriverStatus beforeNewestStatus = lastTwo.get(1).getStatus();
        String newestUpdateNodeId = lastTwo.get(0).getEventSourceNodeId();
        String currentNodeId = currentNodeMetaDataProvider.getCurrentNodeId();
        if (newestStatus != ON_RIDE || beforeNewestStatus != PENDING_ORDER || !newestUpdateNodeId.equals(currentNodeId)) {
            log.warn("Skipping. Candidate is probably busy. " +
                            "newestStatus = {}, beforeNewestStatus = {}" +
                            "{} != {}",
                    newestStatus, beforeNewestStatus, newestUpdateNodeId, currentNodeId);
            return false;
        }

        log.info("Booked strictly");
        return true;
    }
}
