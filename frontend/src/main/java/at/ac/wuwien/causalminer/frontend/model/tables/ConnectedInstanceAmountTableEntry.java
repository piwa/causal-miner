package at.ac.wuwien.causalminer.frontend.model.tables;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConnectedInstanceAmountTableEntry {

    @JsonProperty("origId")
    private String origId;
    @JsonProperty("amount")
    private Long amount;
    @JsonProperty("eventTypeLabel")
    private String eventType;



}