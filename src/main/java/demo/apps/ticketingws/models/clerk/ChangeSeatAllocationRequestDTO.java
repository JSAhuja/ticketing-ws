package demo.apps.ticketingws.models.clerk;

import demo.apps.ticketingws.models.train.TrainSection;
import lombok.Builder;
import lombok.Data;

/**
 * @author jsa000y
 */

@Data
@Builder
public class ChangeSeatAllocationRequestDTO {
    private String currentBookingId;
    private int seatNumberSelected;
    private TrainSection trainSectionSelected;
}
