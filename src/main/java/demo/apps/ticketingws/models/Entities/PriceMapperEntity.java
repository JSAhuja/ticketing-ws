package demo.apps.ticketingws.models.Entities;

import lombok.Builder;
import lombok.Data;

/**
 * @author jsa000y
 */

@Builder
@Data
public class PriceMapperEntity {
    String fromStation;
    String destinationStation;
    double costASeat;
}
