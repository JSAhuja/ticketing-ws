package demo.apps.ticketingws.repository;

import demo.apps.ticketingws.common.DbStore;
import demo.apps.ticketingws.models.Entities.BookingActionLogEntity;
import demo.apps.ticketingws.models.clerk.BookingActions;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

/**
 * @author jsa000y
 */

@Repository
public class BookingActionLogRepo {

    public void addBookingActionLogs(String bookingID, BookingActions bookingAction, String s) {
        BookingActionLogEntity logEntity = BookingActionLogEntity.builder()
                .bookingID(bookingID)
                .actionComments(s)
                .actionDtm(ZonedDateTime.now())
                .action(bookingAction).build();
        DbStore.BOOKING_LOG_ENTITY.add(logEntity);
    }

}
