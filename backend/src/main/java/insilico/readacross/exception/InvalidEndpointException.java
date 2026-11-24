package insilico.readacross.exception;

import org.springframework.http.HttpStatus;

public class InvalidEndpointException extends RestOperationException {

    public InvalidEndpointException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
