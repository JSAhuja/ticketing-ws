package demo.apps.ticketingws.repository;

import demo.apps.ticketingws.common.DbStore;
import demo.apps.ticketingws.models.Entities.TrainOccupancyEntity;
import demo.apps.ticketingws.models.train.TrainSection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static demo.apps.ticketingws.common.DbStore.TRAIN_SEAT_OCCUPANCY_TABLE;

/**
 * @author jsa000y
 */

@Repository
public class TrainSeatRepo {

    public Optional<TrainOccupancyEntity> findById(String seatId) {
        if (TRAIN_SEAT_OCCUPANCY_TABLE.containsKey(seatId)) {
            return Optional.of(TRAIN_SEAT_OCCUPANCY_TABLE.get(seatId));
        } else {
            return Optional.empty();
        }
    }

    public long getSeatsAvailableCount() {
        return TRAIN_SEAT_OCCUPANCY_TABLE.values().stream().filter(record -> !record.isAllocated()).count();
    }

    public void save(TrainOccupancyEntity trainOccupancyEntity) {
        DbStore.TRAIN_SEAT_OCCUPANCY_TABLE.put(trainOccupancyEntity.getId(), trainOccupancyEntity);
    }

    public List<TrainOccupancyEntity> findBySectionId(TrainSection id) {
        return DbStore.TRAIN_SEAT_OCCUPANCY_TABLE.values().stream().filter(record -> record.getSection().equals(id) && record.isAllocated()).collect(Collectors.toList());
    }

    public Optional<TrainOccupancyEntity> findTrainSeatDetailsByBookingID(String bookingId) {
        return TRAIN_SEAT_OCCUPANCY_TABLE.values().stream().filter(record -> record.getBookingId() != null && record.getBookingId().equals(bookingId)).findFirst();
    }

    public  Optional<TrainOccupancyEntity> getSeatsAvailableSeatDetails() {
        return TRAIN_SEAT_OCCUPANCY_TABLE.values().stream().filter(row -> !row.isAllocated()).findFirst() ;
    }
}
