package demo.apps.ticketingws.models.clerk;

import demo.apps.ticketingws.models.train.TrainSection;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author jsa000y
 */

@Data
@Builder
public class ChangeSeatAllocationRequestDTO {
    @NotNull(message = "Booking Id can't be empty")
    private String currentBookingId;

    @Min(1)
    private int seatNumberSelected;

    @NotNull(message = "Train section should not be empty")
    private TrainSection trainSectionSelected;
}
