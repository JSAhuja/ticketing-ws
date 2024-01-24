package demo.apps.ticketingws.controllers;

import demo.apps.ticketingws.models.clerk.BookingActions;
import demo.apps.ticketingws.models.clerk.ChangeSeatAllocationRequestDTO;
import demo.apps.ticketingws.services.BookingClerkServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author jsa000y
 */

@RestController
@RequestMapping("/back-office/")
public class BookingClerkController {

    @Autowired
    BookingClerkServices bookingClerkServices;

    /**
     * This end point is to deallocate a seat or cancel the booking
     *
     * @param id is the booking id. If the id is found it will return 200 as response or throw 400 bad request with the validation error message
     */
    @DeleteMapping("/seat")
    public void deallocateSeat(@RequestParam(name = "id") @NotNull String id) {
        bookingClerkServices.deallocateSeat(id, BookingActions.CANCEL);
    }

    /**
     * This is when a passenger wants to change a seat within the same compartment.
     * The same can be use if a passenger want to change the seat to another compartment.
     *
     * @param requestDTO takes in the details about the current booking and details of the compartment and the seat number which the user has picked up
     * @return the string
     */
    @PutMapping("/change-seat")
    public String changeSeat(@RequestBody @Valid ChangeSeatAllocationRequestDTO requestDTO) {
        return bookingClerkServices.changeSeat(requestDTO);
    }
}
