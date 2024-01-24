package demo.apps.ticketingws.models.Entities;

import demo.apps.ticketingws.models.clerk.BookingActions;
import lombok.Builder;

import java.time.ZonedDateTime;

/**
 * @author jsa000y
 */

@Builder
public class BookingActionLogEntity {
    private String bookingID;
    private String performedBy;
    private BookingActions action;
    private String actionComments;
    private ZonedDateTime actionDtm;
}
