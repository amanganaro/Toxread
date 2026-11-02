package insilico.readacross.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class RaRequest {
    @JsonProperty("smiles")
    private String smiles;
    @JsonProperty("endpoint")
    private String endpoint;
    @JsonProperty("numSimilarMolecules")
    private int numSimilarMolecules;
}
