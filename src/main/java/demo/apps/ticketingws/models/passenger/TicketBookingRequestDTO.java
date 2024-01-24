package demo.apps.ticketingws.models.passenger;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jsa000y
 */
@Data
@Builder
public class TicketBookingRequestDTO {
    @NotNull
    private String fromStation;
    @NotNull
    private String destinationStation;
    @NotNull
    private PassengerUser userDetails;
}
