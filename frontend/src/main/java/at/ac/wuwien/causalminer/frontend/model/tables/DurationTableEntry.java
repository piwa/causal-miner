package at.ac.wuwien.causalminer.frontend.model.tables;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
public class DurationTableEntry {

    @JsonProperty("instanceIdFull")
    private String instanceIdFull;
    @JsonProperty("instanceIdLabel")
    private String instanceId;

    private String startId;
    private String endId;
    private String endEventType;

    private String pathStartDate;
    private String pathEndDate;

    private String totalDurationMinutes;

}
