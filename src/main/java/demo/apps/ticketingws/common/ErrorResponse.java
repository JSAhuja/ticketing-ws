package demo.apps.ticketingws.common;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jsa000y
 */
@Data
@NoArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;

    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
