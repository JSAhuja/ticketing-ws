package demo.apps.ticketingws.services;

import demo.apps.ticketingws.models.Entities.BookingEntity;
import demo.apps.ticketingws.models.Entities.PriceMapperEntity;
import demo.apps.ticketingws.models.Entities.TrainOccupancyEntity;
import demo.apps.ticketingws.models.passenger.TicketBookingRequestDTO;
import demo.apps.ticketingws.models.passenger.TicketReceiptResponseDTO;
import demo.apps.ticketingws.repository.BookingActionLogRepo;
import demo.apps.ticketingws.repository.BookingRepo;
import demo.apps.ticketingws.repository.PriceMapperRepo;
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
public class PassengerService {

    @Autowired
    BookingCommonService bookingHelperService;
    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    BookingActionLogRepo actionLogRepo;
    @Autowired
    TrainSeatRepo trainSeatRepo;
    @Autowired
    PriceMapperRepo priceMapperRepo;

    public String bookTicket(TicketBookingRequestDTO requestDTO) {
        Optional<PriceMapperEntity> price = priceMapperRepo.getPrice(requestDTO.getFromStation(), requestDTO.getDestinationStation());
        if (price.isEmpty()) {
            throw new ValidationException("We are not serving the station you have selected");
        }
        if (bookingHelperService.isSeatAvailable()) {
            return assignSeat(requestDTO, price.get());
        } else {
            throw new ValidationException("Train is Full");
        }
    }

    private BookingEntity transformerTicketRequestToEntity(TicketBookingRequestDTO requestDTO, PriceMapperEntity price) {
        return BookingEntity.builder()
                .ticketCost(price.getCostASeat())
                .user(requestDTO.getUserDetails())
                .isActive(true)
                .build();
    }

    private String assignSeat(TicketBookingRequestDTO requestDTO, PriceMapperEntity price) {
        String availableSeatID = bookingHelperService.getNextAvailableSectionSeat();
        String bookingId = bookingRepo.save(transformerTicketRequestToEntity(requestDTO, price));
        bookingHelperService.seatAllocationInTrain(availableSeatID, bookingId);
        return bookingId;
    }


    public TicketReceiptResponseDTO getTicketReceipt(String id) {
        Optional<TrainOccupancyEntity> trainSeatDetails = trainSeatRepo.findTrainSeatDetailsByBookingID(id);
        if (trainSeatDetails.isEmpty()) {
            throw new ValidationException("No allocated Train seat found matching with the booking Id");
        }
        Optional<BookingEntity> bookingDetails = bookingRepo.findBookingDetailsByBookingId(id);
        if (bookingDetails.isPresent()) {
            return transformEntityToReceiptResponseDTO(trainSeatDetails.get(), bookingDetails.get());
        } else {
            throw new ValidationException("No matching booking found");
        }
    }

    private TicketReceiptResponseDTO transformEntityToReceiptResponseDTO(TrainOccupancyEntity trainDetails, BookingEntity booking) {
        return TicketReceiptResponseDTO.builder()
                .bookingId(booking.getId())
                .amountPaid(booking.getTicketCost())
                .passengerDetails(booking.getUser())
                .amountPaid(booking.getTicketCost())
                .destinationStation(trainDetails.getToStation())
                .fromStation(trainDetails.getFromStation())
                .seatNumber(trainDetails.getSeatNumber())
                .section(trainDetails.getSection())
                .build();
    }
}
