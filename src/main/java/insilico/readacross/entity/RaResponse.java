package insilico.readacross.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class RaResponse {
    @JsonProperty("endpoint")
    private String endpoint;
    @JsonProperty("molecule")
    private RootMoleculeDTO molecule;
    @JsonProperty("similarMolecules")
    private List<SimilarMoleculeDTO> similarMolecules;
    @JsonProperty("rules")
    private List<RuleDTO> rules;
}
