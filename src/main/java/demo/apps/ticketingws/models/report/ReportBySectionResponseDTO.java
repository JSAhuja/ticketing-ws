package demo.apps.ticketingws.models.report;

import demo.apps.ticketingws.models.passenger.PassengerUser;
import demo.apps.ticketingws.models.train.TrainSection;
import lombok.Builder;
import lombok.Data;

/**
 * @author jsa000y
 */

@Builder
@Data
public class ReportBySectionResponseDTO {
    private String bookingId;
    private PassengerUser user;
    private int seatNumber;
    private TrainSection section;
}
