package demo.apps.ticketingws.common;

import demo.apps.ticketingws.services.BookingCommonService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author jsa000y
 */
@Configuration
public class ApplicationStartup {

    @Autowired
    BookingCommonService bookingCommonService;

    @PostConstruct
    public void initTrainOccupancyTable() {
        bookingCommonService.createSeatsForTrain();
        bookingCommonService.stationToPriceMapper();
    }
}
