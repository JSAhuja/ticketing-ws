package demo.apps.ticketingws.models.Entities;

import demo.apps.ticketingws.models.train.TrainSection;
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
public class TrainOccupancyEntity {
    private String id;
    private String fromStation;
    private String toStation;
    int seatNumber;
    private TrainSection section;
    private boolean isAllocated = true;
    private String bookingId;
}
