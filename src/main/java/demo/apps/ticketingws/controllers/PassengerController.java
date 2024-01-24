package demo.apps.ticketingws.controllers;

import demo.apps.ticketingws.models.passenger.TicketBookingRequestDTO;
import demo.apps.ticketingws.models.passenger.TicketReceiptResponseDTO;
import demo.apps.ticketingws.services.PassengerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@Slf4j
@RequestMapping("/passenger")
public class PassengerController {

    @Autowired
    PassengerService passengerService;


    /**
     * This end point is to book a set for the passenger
     *
     * @param requestDTO the request dto will be provided as a part of post call body
     * @return the booking id as string
     */
    @PostMapping("/book-ticket")
    public String bookTicket(@RequestBody @Valid TicketBookingRequestDTO requestDTO) {
        return passengerService.bookTicket(requestDTO);
    }


    /**
     * This end point is to get the details of the Ticket as receipt
     *
     * @param id is the booking ID
     * @return the receipt
     */
    @GetMapping("/receipt")
    public TicketReceiptResponseDTO getReceipt(@RequestParam(name = "id") @NotNull String id) {
        return passengerService.getTicketReceipt(id);
    }
}
