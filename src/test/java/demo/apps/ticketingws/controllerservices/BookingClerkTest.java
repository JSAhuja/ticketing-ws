package demo.apps.ticketingws.controllerservices;

import demo.apps.ticketingws.TicketingWsApplication;
import demo.apps.ticketingws.models.Entities.BookingEntity;
import demo.apps.ticketingws.models.Entities.TrainOccupancyEntity;
import demo.apps.ticketingws.models.clerk.ChangeSeatAllocationRequestDTO;
import demo.apps.ticketingws.models.passenger.PassengerUser;
import demo.apps.ticketingws.models.passenger.TicketBookingRequestDTO;
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

import java.io.IOException;
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
class BookingClerkTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @MockBean TrainSeatRepo trainSeatRepo;
    @MockBean BookingRepo bookingRepo;

    private static final String CONTROLLER_BASE_URL="/back-office";

    /**
     * This test to validate the de-allocation of the seat.
     * The expectation is the test data is correct as according to mocks.
     */
    @Test
    void deallocateSeat(){
        String bookingId="46f8e96f-bb17-4347-a8c4-4d8674ebbef7";
        TrainOccupancyEntity trainOccupancyEntityMocked=TrainOccupancyEntity
                .builder()
                .id("B-1")
                .seatNumber(1)
                .section(TrainSection.B)
                .bookingId(bookingId)
                .toStation("LONDON")
                .fromStation("FRANCE")
                .isAllocated(true)
                .build();
        when(trainSeatRepo.findTrainSeatDetailsByBookingID(anyString())).thenReturn(Optional.of(trainOccupancyEntityMocked));
        when(trainSeatRepo.getSeatsAvailableSeatDetails()).thenReturn(Optional.of(trainOccupancyEntityMocked));
        when(trainSeatRepo.findTrainSeatDetailsByBookingID(anyString())).thenReturn(Optional.of(trainOccupancyEntityMocked));

        BookingEntity bookingEntityMocked=BookingEntity
                .builder()
                .id(bookingId)
                .isActive(true)
                .ticketCost(12)
                .user(PassengerUser.builder().firstName("firstName").lastName("lastName").email("email@email.com").build())
                .build();
        when(bookingRepo.findBookingDetailsByBookingId(bookingId)).thenReturn(Optional.of(bookingEntityMocked));

//        when(bookingRepo.deallocateBooking(anyString())).thenReturn(true);
        HttpEntity<Object> request = new HttpEntity<>(StringUtils.EMPTY);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/seat");
        builder.queryParam("id", bookingId);
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * This test to validate de-allocation scenario.
     * In this scenario the user requested a invalid booking Id and Api si expected to return an 400 error message
     */
    @Test
    void deallocateSeat_withInvalidBookingID(){
        when(trainSeatRepo.findTrainSeatDetailsByBookingID(anyString())).thenReturn(Optional.empty());
        HttpEntity<Object> request = new HttpEntity<>(StringUtils.EMPTY);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/seat");
        builder.queryParam("id", "46f8e96f-bb17-4347-a8c4-4d8674ebbef7");
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).contains("Booking Id not found for the train");
    }

    /**
     * This test to validate scenario when new seat want to change the seat and the newly selected seat is available.
     */
    @Test
    void changeSeat_newSeatIsAvailable() throws IOException {
        final BookingEntity bookingEntityMocked = TestDataUtil.getTypeFromJson("BookingEntity.json", BookingEntity.class);
        TrainOccupancyEntity trainOccupancyEntityMocked = TestDataUtil.getTypeFromJson("TrainOccupancyEntity.json", TrainOccupancyEntity.class);
        trainOccupancyEntityMocked.setAllocated(false);

        when(trainSeatRepo.findById(any())).thenReturn(Optional.of(trainOccupancyEntityMocked));
        when(bookingRepo.findBookingDetailsByBookingId(any())).thenReturn(Optional.of(bookingEntityMocked));
        when(trainSeatRepo.findTrainSeatDetailsByBookingID(any())).thenReturn(Optional.of(trainOccupancyEntityMocked));
//        when(bookingRepo.deallocateBooking(bookingEntityMocked.getId())).thenReturn(true);

        BookingEntity newTicket = TestDataUtil.getTypeFromJson("BookingEntity.json", BookingEntity.class);
        String newBookingId="111-222-3333";
        newTicket.setId(newBookingId);
        when(bookingRepo.save(any())).thenReturn(newBookingId);
        ChangeSeatAllocationRequestDTO requestDTO=ChangeSeatAllocationRequestDTO.builder()
                .seatNumberSelected(2)
                .currentBookingId(bookingEntityMocked.getId())
                .trainSectionSelected(TrainSection.B).build();

        HttpEntity<Object> request = new HttpEntity<>(requestDTO);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/change-seat");
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.PUT, request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotEqualTo(bookingEntityMocked.getId());
    }

    /**
     * This test to validate scenario when new seat want to change the seat and the provided booking id is invalid.
     */

    @Test
    void changeSeat_withInvalidBookingID() throws IOException {
        final BookingEntity bookingEntityMocked = TestDataUtil.getTypeFromJson("BookingEntity.json", BookingEntity.class);
        when(bookingRepo.findBookingDetailsByBookingId(anyString())).thenReturn(Optional.empty());
        ChangeSeatAllocationRequestDTO requestDTO=ChangeSeatAllocationRequestDTO.builder()
                .seatNumberSelected(2)
                .currentBookingId("INVALID-BOOKING-ID")
                .trainSectionSelected(TrainSection.B).build();
        HttpEntity<Object> request = new HttpEntity<>(requestDTO);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/change-seat");
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.PUT, request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).contains("Invalid booking ID provided");
    }

    /**
     * This test to validate scenario when new seat selected is already occupied
     */
    @Test
    void changeSeat_whenSpecifiedSeatIsAlreadyOccupied() throws IOException {
        final BookingEntity bookingEntityMocked = TestDataUtil.getTypeFromJson("BookingEntity.json", BookingEntity.class);
        when(bookingRepo.findBookingDetailsByBookingId(bookingEntityMocked.getId())).thenReturn(Optional.of(bookingEntityMocked));

        TrainOccupancyEntity trainOccupancyEntityMocked = TestDataUtil.getTypeFromJson("TrainOccupancyEntity.json", TrainOccupancyEntity.class);
        trainOccupancyEntityMocked.setAllocated(true);
        trainOccupancyEntityMocked.setBookingId(bookingEntityMocked.getId());

        when(trainSeatRepo.findById(any())).thenReturn(Optional.of(trainOccupancyEntityMocked));
        ChangeSeatAllocationRequestDTO requestDTO=ChangeSeatAllocationRequestDTO.builder()
                .seatNumberSelected(2)
                .currentBookingId(trainOccupancyEntityMocked.getBookingId())
                .trainSectionSelected(TrainSection.B).build();
        HttpEntity<Object> request = new HttpEntity<>(requestDTO);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath( CONTROLLER_BASE_URL +"/change-seat");
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(builder.toUriString(), HttpMethod.PUT, request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).contains("Not able to allocate seat in train");

    }

}
