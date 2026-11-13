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
    private String endpoint;
    private String prediction;
    private MoleculeDTO molecule;
    private List<SimilarMoleculeDTO> similarMolecules;
    private List<RuleDTO> rules;
    private List<InterpolationDTO> interpolation;
}
