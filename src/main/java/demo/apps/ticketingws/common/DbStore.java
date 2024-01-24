package demo.apps.ticketingws.common;

import demo.apps.ticketingws.models.Entities.BookingActionLogEntity;
import demo.apps.ticketingws.models.Entities.BookingEntity;
import demo.apps.ticketingws.models.Entities.PriceMapperEntity;
import demo.apps.ticketingws.models.Entities.TrainOccupancyEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsa000y
 */
public class DbStore {
    public static Map<String, TrainOccupancyEntity> TRAIN_SEAT_OCCUPANCY_TABLE = new LinkedHashMap();
    public static Map<String, BookingEntity> BOOKING_TABLE = new HashMap();
    public static List<BookingActionLogEntity> BOOKING_LOG_ENTITY = new ArrayList<>();
    public static List<PriceMapperEntity> PRICE_MAPPER_TABLE = new ArrayList<>();

}
