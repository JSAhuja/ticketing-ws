package demo.apps.ticketingws.services;

import demo.apps.ticketingws.models.Entities.BookingEntity;
import demo.apps.ticketingws.models.clerk.BookingActions;
import demo.apps.ticketingws.models.clerk.ChangeSeatAllocationRequestDTO;
import demo.apps.ticketingws.models.passenger.PassengerUser;
import demo.apps.ticketingws.repository.BookingActionLogRepo;
import demo.apps.ticketingws.repository.BookingRepo;
import demo.apps.ticketingws.repository.TrainSeatRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.Optional;

/**
 * @author jsa000y
 */

@Service
@Slf4j
public class BookingClerkServices {
    @Autowired
    BookingCommonService bookingCommonService;

    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    BookingActionLogRepo actionLogRepo;
    @Autowired
    TrainSeatRepo trainSeatRepo;


    public void deallocateSeat(String id,BookingActions action) {
        log.info("Trying to find the booking with id:{}", id);
        bookingCommonService.seatDeAllocationInTrainByBookingId(id);
        bookingCommonService.deAllocateBooking(id, action, "");
        log.info("Successfully deallocated the booking");
    }

    public String changeSeat(ChangeSeatAllocationRequestDTO requestDTO) {
        log.info("Request received to change the seat for the booking id:{}",requestDTO.getCurrentBookingId());
// Step1 Find if valid booking ID
        final Optional<BookingEntity> bookingDetailsById = bookingCommonService.getBookingDetailsById(requestDTO.getCurrentBookingId());
        if (bookingDetailsById.isEmpty()) {
            throw new ValidationException("Invalid booking ID provided");
        }
        log.info("Valid booking id:{} found in DB",requestDTO.getCurrentBookingId());
// Step2 Add new entry in Booking;
        final BookingEntity bookingEntityBasedOnRequestDto = bookingDetailsById.get();
        log.info("Updating the booking repo with as a replacement for old booking id:{}",requestDTO.getCurrentBookingId());
        BookingEntity newBookingEntity = BookingEntity.builder()
                .ticketCost(bookingEntityBasedOnRequestDto.getTicketCost())
                .user(PassengerUser.builder().firstName(bookingEntityBasedOnRequestDto.getUser().getFirstName()).lastName(bookingEntityBasedOnRequestDto.getUser().getLastName()).email(bookingEntityBasedOnRequestDto.getUser().getEmail()).build())
                .isActive(true)
                .build();
        String newTicketId = bookingRepo.save(newBookingEntity);
        log.info("New booking id:{} replacement for the old booking id:{}",newTicketId,requestDTO.getCurrentBookingId());

// Step3 Allocate Seat in Train
        log.info("Trying to allocate seat corresponding to the new booking id:{}",newTicketId);
        boolean seatAllocationInTrain = bookingCommonService.seatAllocationInTrain(bookingCommonService.generateTrainSeatId(requestDTO.getTrainSectionSelected(), requestDTO.getSeatNumberSelected()), newTicketId);
        if (!seatAllocationInTrain) {
            throw new ValidationException("Not able to allocate seat in train");
        }
        log.info("Allocated successfully seat corresponding to the new booking id:{}",newTicketId);
// Step4 Deallocate Seat
        deallocateSeat(requestDTO.getCurrentBookingId(),BookingActions.SEAT_CHANGE);
        log.info("Deallocation of the old booking ID:{} is completed",requestDTO.getCurrentBookingId());
        return newTicketId;
    }
}
