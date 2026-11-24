package insilico.readacross.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class InterpolationDTO {
    private String axisXTitle;
    private String axisYTitle;
    private double targetLineX;
    private double targetLineY;
    private List<InterpolationPoint> points;
}
