package at.ac.wuwien.causalminer.frontend.model.frappeGantt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FrappeGanttEntry {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("start")
    private String startDate;

    @JsonProperty("end")
    private String endDate;

    @JsonProperty("progress")
    private Double progress = 100.0;

    @JsonProperty("dependencies")
    private String dependencies = "";
}
