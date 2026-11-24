package insilico.readacross.controller;

import insilico.readacross.exception.RestOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class AbstractRestController {

    protected AbstractRestController() {}

    protected ResponseEntity<?> executeCommandOrFail(RestControllerCommand cmd) {
        try {
            return new ResponseEntity<>(cmd.get(), HttpStatus.OK);
        } catch (RestOperationException exception) {
            return exception.getResponseEntityError();
        }
    }
}
