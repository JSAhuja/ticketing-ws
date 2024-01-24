package demo.apps.ticketingws.services;

import demo.apps.ticketingws.models.Entities.BookingEntity;
import demo.apps.ticketingws.models.Entities.TrainOccupancyEntity;
import demo.apps.ticketingws.models.report.ReportBySectionResponseDTO;
import demo.apps.ticketingws.models.train.TrainSection;
import demo.apps.ticketingws.repository.BookingActionLogRepo;
import demo.apps.ticketingws.repository.BookingRepo;
import demo.apps.ticketingws.repository.TrainSeatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jsa000y
 */

@Service
public class ReportServices {

    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    BookingActionLogRepo actionLogRepo;
    @Autowired
    TrainSeatRepo trainSeatRepo;


    public List<ReportBySectionResponseDTO> generateReportBySection(TrainSection section) {
        List<TrainOccupancyEntity> bySectionId = trainSeatRepo.findBySectionId(section);
        Map<String, BookingEntity> stringBookingEntityMap = bookingRepo.findBookingsByIdIn(bySectionId.stream().map(TrainOccupancyEntity::getBookingId)
                .collect(Collectors.toList()));
        List<ReportBySectionResponseDTO> list = new ArrayList<>();
        if (!bySectionId.isEmpty()) {
            bySectionId.forEach(record -> list.add(transformerToReportBySection(record, stringBookingEntityMap.get(record.getBookingId()))));
        }
        return list;
    }

    private ReportBySectionResponseDTO transformerToReportBySection(TrainOccupancyEntity trainRecord, BookingEntity bookingRecord) {
        return ReportBySectionResponseDTO.builder()
                .section(trainRecord.getSection())
                .user(bookingRecord.getUser())
                .seatNumber(trainRecord.getSeatNumber())
                .bookingId(bookingRecord.getId()).build();
    }
}
