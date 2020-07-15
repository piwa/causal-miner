package at.ac.wuwien.causalminer.frontend.model.tables;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProcessInstanceViolationsTableEntry {

    private String violationType;
    private String violationText;

    private String startNodeLabel;
    private String endNodeLabel;



}