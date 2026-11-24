package insilico.readacross.controller;

import insilico.readacross.service.ToxreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin(origins = "*")
public class ToxreadController extends AbstractRestController{

    @Autowired
    private ToxreadService toxreadService;

    @GetMapping(path = "/endpoints", produces = "application/json")
    public ResponseEntity<?> retrieveEndpoints() {
        return executeCommandOrFail(() -> toxreadService.retrieveEndpoints());
    }

    @PostMapping(path = "/compute", produces = "application/json")
    public ResponseEntity<?> startRA(@RequestBody String smiles, @RequestParam String endpoint,
                                     @RequestParam int numSimilarMolecules) {
        return executeCommandOrFail(() ->
                toxreadService.startRA(smiles, endpoint, numSimilarMolecules));
    }
}
