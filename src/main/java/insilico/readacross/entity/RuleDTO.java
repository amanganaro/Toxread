package insilico.readacross.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import insilico.core.alerts.Alert;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class RuleDTO {
    private String name;
    private String description;
    private String smarts;
}
