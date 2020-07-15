package at.ac.wuwien.causalminer.frontend.model.tables;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProcessViewTableEntry {

    @JsonProperty("instanceIdFull")
    private String instanceIdFull;
//    @JsonProperty("firmNr")
//    private String firmNr;
    @JsonProperty("origId")
    private String origId;


}
