package demo.apps.ticketingws.repository;

import demo.apps.ticketingws.common.DbStore;
import demo.apps.ticketingws.models.Entities.PriceMapperEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author jsa000y
 */

@Repository
public class PriceMapperRepo {

    public void save(PriceMapperEntity entity) {
        DbStore.PRICE_MAPPER_TABLE.add(entity);
    }

    public Optional<PriceMapperEntity> getPrice(String fromStation, String toStation) {
        final String fromStationUpper = fromStation.toUpperCase();
        final String toStationUpper = toStation.toUpperCase();
        return DbStore.PRICE_MAPPER_TABLE.stream()
                .filter(row -> row.getFromStation().equals(fromStationUpper) && row.getDestinationStation().equals(toStationUpper))
                .findFirst();
    }
}
