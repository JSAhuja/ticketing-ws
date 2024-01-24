package demo.apps.ticketingws.controllerservices;

import demo.apps.ticketingws.TicketingWsApplication;
import demo.apps.ticketingws.models.Entities.BookingEntity;
import demo.apps.ticketingws.models.Entities.TrainOccupancyEntity;
import demo.apps.ticketingws.models.passenger.PassengerUser;
import demo.apps.ticketingws.models.passenger.TicketBookingRequestDTO;
import demo.apps.ticketingws.models.passenger.TicketReceiptResponseDTO;
import demo.apps.ticketingws.models.train.TrainSection;
import demo.apps.ticketingws.repository.BookingRepo;
import demo.apps.ticketingws.repository.TrainSeatRepo;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author jsa000y
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TicketingWsApplication.class)
class PassengerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean BookingRepo bookingRepo;
    @MockBean TrainSeatRepo trainSeatRepo;

    private static final String CONTROLLER_BASE_URL="/passenger";

    /**
     * This test to validate scenario when a hits the api to book a new ticket.
     */
    @Test
    void bookTicketTest(){
        TicketBookingRequestDTO requestDTO=TicketBookingRequestDTO.builder()
                .destinationStation("France").fromStation("London")
                .userDetails(PassengerUser.builder().firstName("Jivtesh").lastName("Ahuja").email("email.gmail.com").build())
                .build();
        TrainOccupancyEntity trainOccupancyEntity = TrainOccupancyEntity.builder().isAllocated(false)
                .fromStation(requestDTO.getFromStation()).toStation(requestDTO.getDestinationStation())
                .id("A-1").section(TrainSection.A).seatNumber(1).build();

        when(trainSeatRepo.getSeatsAvailableCount()).thenReturn(20L);
        when(trainSeatRepo.getSeatsAvailableSeatDetails()).thenReturn(Optional.of(trainOccupancyEntity));
        when(trainSeatRepo.findById("A-1")).thenReturn(Optional.of(trainOccupancyEntity));

        HttpEntity<Object> request = new HttpEntity<>(requestDTO);

        String bookingId="46f8e96f-bb17-4347-a8c4-4d8674ebbef7";
        when(bookingRepo.save(any())).thenReturn(bookingId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/book-ticket");
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains(bookingId);
    }

    /**
     * This test to validate scenario when the To and From destination mapping is avaialable for the train
     */
    @Test
    void bookTicketTest_statingMappingNotPresent(){
        TicketBookingRequestDTO requestDTO=TicketBookingRequestDTO.builder()
                .destinationStation("France").fromStation("DELHI")
                .userDetails(PassengerUser.builder().firstName("Jivtesh").lastName("Ahuja").email("email.gmail.com").build())
                .build();

        HttpEntity<Object> request = new HttpEntity<>(requestDTO);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/book-ticket");
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).contains("We are not serving the station you have selected");
    }

    /**
     * This test to validate scenario when there are no available seats in train. Train is fully occupied
     */
    @Test
    void bookTicketTest_capacityValidator(){
        TicketBookingRequestDTO requestDTO=TicketBookingRequestDTO.builder()
                .destinationStation("France").fromStation("London")
                .userDetails(PassengerUser.builder().firstName("Jivtesh").lastName("Ahuja").email("email.gmail.com").build())
                .build();
        when(trainSeatRepo.getSeatsAvailableCount()).thenReturn(0L);
        HttpEntity<Object> request = new HttpEntity<>(requestDTO);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/book-ticket");
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    /**
     * This test to validate while generation a receipt the booking id is invalid
     */
    @Test
    void getReceipt_withNoBookingIdInTrainTable(){
        HttpEntity<Object> request = new HttpEntity<>(StringUtils.EMPTY);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/receipt");
        builder.queryParam("id", "someInvalidString");
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).contains("No allocated Train seat found matching with the booking Id");
    }

    /**
     * This is a positive scenario when the has provided correct booking id for receipt to generate.
     */
    @Test
    void getReceipt_withValidBooking(){
        String bookingId="THIS-IS-RANDOM-BOOKING-ID";
        TrainOccupancyEntity trainOccupancyEntityMocked=TrainOccupancyEntity
                .builder()
                .id("B-1")
                .seatNumber(1)
                .section(TrainSection.B)
                .bookingId(bookingId)
                .toStation("FRANCE")
                .fromStation("PARIS")
                .isAllocated(true)
                .build();
        when(trainSeatRepo.findTrainSeatDetailsByBookingID(anyString())).thenReturn(Optional.of(trainOccupancyEntityMocked));
        BookingEntity bookingEntityMocked=BookingEntity
                .builder()
                .id(bookingId)
                .isActive(true)
                .ticketCost(12)
                .user(PassengerUser.builder().firstName("firstName").lastName("lastName").email("email@email.com").build())
                .build();
        when(bookingRepo.findBookingDetailsByBookingId(anyString())).thenReturn(Optional.of(bookingEntityMocked));
        HttpEntity<Object> request = new HttpEntity<>(StringUtils.EMPTY);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/receipt");
        builder.queryParam("id", bookingId);
        ResponseEntity<TicketReceiptResponseDTO> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, TicketReceiptResponseDTO.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).getBookingId()).isEqualTo(bookingId);
    }

}
