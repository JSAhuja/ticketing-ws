package demo.apps.ticketingws.services;

import demo.apps.ticketingws.models.Entities.BookingEntity;
import demo.apps.ticketingws.models.Entities.PriceMapperEntity;
import demo.apps.ticketingws.models.Entities.TrainOccupancyEntity;
import demo.apps.ticketingws.models.clerk.BookingActions;
import demo.apps.ticketingws.models.train.TrainSection;
import demo.apps.ticketingws.repository.BookingActionLogRepo;
import demo.apps.ticketingws.repository.BookingRepo;
import demo.apps.ticketingws.repository.PriceMapperRepo;
import demo.apps.ticketingws.repository.TrainSeatRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.Optional;

/**
 * @author jsa000y
 */

@Service
@Slf4j
public class BookingCommonService {

    @Value("${admin.train.maxSeatPerCompartment}")
    private int perSectionSeatCount;

    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    BookingActionLogRepo actionLogRepo;

    @Autowired
    TrainSeatRepo trainSeatRepo;

    @Autowired
    PriceMapperRepo priceMapperRepo;

    public boolean isSeatAvailable() {
        log.info("Trying to retrieve the number of available seats");
        long seatsAvailableCount = trainSeatRepo.getSeatsAvailableCount();
        log.info("Total seats remaining count is {}", seatsAvailableCount);
        return seatsAvailableCount > 0;
    }

    public String getNextAvailableSectionSeat() {
        log.info("Trying to retrieve the next available seat");
        Optional<TrainOccupancyEntity> seatsAvailableSeatDetails = trainSeatRepo.getSeatsAvailableSeatDetails();
        if(seatsAvailableSeatDetails.isPresent()){
            log.info("Availability found");
            return seatsAvailableSeatDetails.get().getId();
        }
        else {
            throw new ValidationException("Train is Full");
        }
    }

    public boolean seatAllocationInTrain(String seatId, String bookingID) {
        log.info("Request received to allocate seat:{} with booking id:{}",seatId,bookingID);
        Optional<TrainOccupancyEntity> trainSeatRepoById = trainSeatRepo.findById(seatId);
        if (trainSeatRepoById.isPresent() && !trainSeatRepoById.get().isAllocated()) {
            TrainOccupancyEntity trainOccupancyEntity = trainSeatRepoById.get();
            trainOccupancyEntity.setAllocated(true);
            trainOccupancyEntity.setBookingId(bookingID);
            trainSeatRepo.save(trainOccupancyEntity);
            actionLogRepo.addBookingActionLogs(bookingID,BookingActions.ALLOCATED,"Ticket is booked");
            log.info("Allocation of seat:{} with booking id:{} is successful",seatId,bookingID);
            return true;
        } else {
            return false;
        }
    }

    public void seatDeAllocationInTrainByBookingId(String bookingID) {
        log.info("Request received to deallocate seat corresponding to booking id:{}",bookingID);
        Optional<TrainOccupancyEntity> recordByBookingID = trainSeatRepo.findTrainSeatDetailsByBookingID(bookingID);
        log.info("Valid Booking id:{} found",bookingID);
        if (recordByBookingID.isPresent()) {
            recordByBookingID.get().setAllocated(false);
            recordByBookingID.get().setBookingId("");
            trainSeatRepo.save(recordByBookingID.get());
            log.info("De-allocation for Booking id:{} is successfully completed in TrainSeat repo",bookingID);
        } else {
            throw new ValidationException("Booking Id not found for the train");
        }
    }

    public void deAllocateBooking(String bookingId, BookingActions action, String s) {
        log.info("Request received to deallocate seat corresponding to booking id:{}",bookingId);
        Optional<BookingEntity> bookingDetailsByBookingId = bookingRepo.findBookingDetailsByBookingId(bookingId);
        if (bookingDetailsByBookingId.isPresent() && bookingDetailsByBookingId.get().isActive()) {
            log.info("Valid Booking id:{} found",bookingId);
            BookingEntity bookingEntity = bookingDetailsByBookingId.get();
            bookingEntity.setActive(false);
            bookingRepo.save(bookingEntity);
            log.info("De-allocation for Booking id:{} is successfully completed in TrainSeat repo",bookingId);
            actionLogRepo.addBookingActionLogs(bookingId, action, s);
        } else
            throw new ValidationException("Failed in deallocating the booking");
    }

    public void createSeatsForTrain() {
        for (int j = 0; j < TrainSection.values().length; j++) {
            for (int i = 1; i <= perSectionSeatCount; i++) {
                TrainOccupancyEntity trainOccupancyEntity = TrainOccupancyEntity.builder()
                        .isAllocated(false)
                        .section(TrainSection.values()[j])
                        .fromStation("London")
                        .toStation("FRANCE")
                        .seatNumber(i)
                        .id(generateTrainSeatId(TrainSection.values()[j], i))
                        .build();
                trainSeatRepo.save(trainOccupancyEntity);
            }
        }
    }

    public void stationToPriceMapper() {
        final PriceMapperEntity build = PriceMapperEntity.builder()
                .fromStation("LONDON")
                .destinationStation("FRANCE")
                .costASeat(12)
                .build();
        priceMapperRepo.save(build);
    }

    public String generateTrainSeatId(TrainSection trainSection, int seatNumber) {
        return trainSection + "-" + seatNumber;
    }

    public Optional<BookingEntity> getBookingDetailsById(String currentBookingId) {
        return bookingRepo.findBookingDetailsByBookingId(currentBookingId);
    }

}
