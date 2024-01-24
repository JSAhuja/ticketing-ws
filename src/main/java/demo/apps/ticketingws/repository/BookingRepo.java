package demo.apps.ticketingws.repository;

import demo.apps.ticketingws.common.DbStore;
import demo.apps.ticketingws.models.Entities.BookingEntity;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static demo.apps.ticketingws.common.DbStore.BOOKING_TABLE;

/**
 * @author jsa000y
 */

@Repository
public class BookingRepo {

    public String save(BookingEntity entry) {
        if (ObjectUtils.isNotEmpty(entry) && ObjectUtils.isEmpty(entry.getId())) {
            entry.setId(UUID.randomUUID().toString());
        }
        BOOKING_TABLE.put(entry.getId(), entry);
        return entry.getId();
    }

    public Optional<BookingEntity> findBookingDetailsByBookingId(String bookingId) {
        BookingEntity bookingEntity = BOOKING_TABLE.getOrDefault(bookingId, null);
        if (bookingEntity != null && bookingEntity.isActive()) {
            return Optional.of(bookingEntity);
        } else {
            return Optional.empty();
        }
    }

    public Map<String, BookingEntity> findBookingsByIdIn(List<String> bookingIdList) {
        return DbStore.BOOKING_TABLE.values().stream().filter(row -> bookingIdList.contains(row.getId())).collect(Collectors.toMap(BookingEntity::getId,
                Function.identity()));
    }

}
