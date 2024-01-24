package demo.apps.ticketingws.models.passenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author jsa000y
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerUser {
    @NotNull(message = "User's first name cannot be empty")
    private String firstName;
    @NotNull(message = "User's last Name cannot be empty")
    String lastName;
    @NotNull(message = "Email Address cannot be empty")
    String email;
}
