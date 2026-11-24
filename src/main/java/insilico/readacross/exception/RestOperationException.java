package insilico.readacross.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public abstract class RestOperationException extends Exception {

    @Getter
    private final String message;
    private final HttpStatus httpStatus;

    protected RestOperationException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public ResponseEntity<Map<String,String>> getResponseEntityError() {
        return new ResponseEntity<>(
                Map.of(
                        "result", "error",
                        "message", message
                ), httpStatus
        );
    }
}
