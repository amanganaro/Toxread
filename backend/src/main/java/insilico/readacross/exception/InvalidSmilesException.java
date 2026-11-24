package insilico.readacross.exception;

import org.springframework.http.HttpStatus;

public class InvalidSmilesException extends RestOperationException{

    public InvalidSmilesException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
