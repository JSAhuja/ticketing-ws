package demo.apps.ticketingws.models.Entities;

import demo.apps.ticketingws.models.passenger.PassengerUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jsa000y
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {
    private String id;
    private PassengerUser user;
    private double ticketCost;
    private boolean isActive = true;
}
