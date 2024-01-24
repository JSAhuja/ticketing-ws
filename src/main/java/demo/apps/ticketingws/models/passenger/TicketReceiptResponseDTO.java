package demo.apps.ticketingws.models.passenger;

import demo.apps.ticketingws.models.train.TrainSection;
import lombok.Builder;
import lombok.Data;

/**
 * @author jsa000y
 */
@Data
@Builder
public class TicketReceiptResponseDTO {
    private String bookingId;
    int seatNumber;
    private TrainSection section;
    private String fromStation;
    private String destinationStation;
    private double amountPaid;
    private PassengerUser passengerDetails;
}
