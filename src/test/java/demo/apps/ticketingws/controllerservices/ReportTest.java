package demo.apps.ticketingws.controllerservices;

import demo.apps.ticketingws.TicketingWsApplication;
import demo.apps.ticketingws.models.Entities.BookingEntity;
import demo.apps.ticketingws.models.Entities.TrainOccupancyEntity;
import demo.apps.ticketingws.models.report.ReportBySectionResponseDTO;
import demo.apps.ticketingws.models.train.TrainSection;
import demo.apps.ticketingws.repository.BookingRepo;
import demo.apps.ticketingws.repository.TrainSeatRepo;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author jsa000y
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TicketingWsApplication.class)
class ReportTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @MockBean
    TrainSeatRepo trainSeatRepo;
    @MockBean BookingRepo bookingRepo;

    private static final String CONTROLLER_BASE_URL="/report";

    /**
     * This test to validate the reporting scenario when there is one valid booking
     */
    @Test
    void getBySection_withValidBooking() throws IOException {
        BookingEntity bookingEntityMocked = TestDataUtil.getTypeFromJson("BookingEntity.json", BookingEntity.class);
        TrainOccupancyEntity trainOccupancyEntityMocked = TestDataUtil.getTypeFromJson("TrainOccupancyEntity.json", TrainOccupancyEntity.class);

        when(trainSeatRepo.findBySectionId(TrainSection.A)).thenReturn(Collections.singletonList(trainOccupancyEntityMocked));
        when(bookingRepo.findBookingsByIdIn(any())).thenReturn(Collections.singletonMap(bookingEntityMocked.getId(),bookingEntityMocked));

        HttpEntity<Object> request = new HttpEntity<>(StringUtils.EMPTY);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/by-section");
        builder.queryParam("section", TrainSection.A);
        ResponseEntity<List<ReportBySectionResponseDTO>> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, new ParameterizedTypeReference<List<ReportBySectionResponseDTO>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).hasSize(1);
        assertThat(responseEntity.getBody().get(0).getBookingId()).isEqualTo(bookingEntityMocked.getId());
        assertThat(responseEntity.getBody().get(0).getSection()).isEqualTo(trainOccupancyEntityMocked.getSection());
    }

    /**
     * This test to validate the reporting scenario when there is no available bookings
     * i.e. the particular compartment has full seat availability.
     */
    @Test
    void getBySection_withNoBooking(){
        when(trainSeatRepo.findBySectionId(TrainSection.A)).thenReturn(Collections.emptyList());
        when(bookingRepo.findBookingsByIdIn(any())).thenReturn(Collections.emptyMap());
        HttpEntity<Object> request = new HttpEntity<>(StringUtils.EMPTY);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/by-section");
        builder.queryParam("section", TrainSection.A);
        ResponseEntity<List<ReportBySectionResponseDTO>> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, new ParameterizedTypeReference<List<ReportBySectionResponseDTO>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEmpty();
    }
}
