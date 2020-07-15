package at.ac.wuwien.causalminer.neo4jdb.domain.query.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardinalityInputVariables {

    private String sourceNodeType;
    private String targetNodeType;
    @JsonProperty("nCardinality")
    private Boolean nCardinality;
    private Integer requiredCardinality;

    private CardinalityInputVariables(String sourceNodeType, String targetNodeType, Boolean nCardinality, Integer requiredCardinality) {
        this.sourceNodeType = sourceNodeType;
        this.targetNodeType = targetNodeType;
        this.nCardinality = nCardinality;
        this.requiredCardinality = requiredCardinality;
    }

    public static CardinalityInputVariables buildUnboundCardinality(String sourceNodeType, String targetNodeType) {
        return new CardinalityInputVariables(sourceNodeType, targetNodeType, true, 0);
    }

    public static CardinalityInputVariables buildBoundCardinality(String sourceNodeType, String targetNodeType, Integer requiredCardinality) {
        return new CardinalityInputVariables(sourceNodeType, targetNodeType, false, requiredCardinality);
    }
}
