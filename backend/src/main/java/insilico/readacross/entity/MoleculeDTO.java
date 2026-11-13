package insilico.readacross.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class MoleculeDTO {
    private String cas;
    private String smiles;
    private String image;
    private Double mw;
    private Double logP;
    private boolean logPExperimental;
}
