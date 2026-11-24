package insilico.readacross.exception;

import org.springframework.http.HttpStatus;

public class RaException extends RestOperationException {

    public RaException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
