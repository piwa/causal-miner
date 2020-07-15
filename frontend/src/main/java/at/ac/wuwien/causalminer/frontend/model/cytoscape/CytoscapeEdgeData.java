package at.ac.wuwien.causalminer.frontend.model.cytoscape;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CytoscapeEdgeData {

    private String id;
    private String source;
    private String target;

    private String label = "";      // TODO the cardinality is added via the label -> use the startCardinality and endCardinality parameter for this
    private Boolean asymmetricConflict = false;
    private Double probability = 10.0;

    private String startCardinality = "";
    private String endCardinality = "";
    private String quantity = "";

    private Map<String, String> properties = new HashMap<>();

    public CytoscapeEdgeData(String source, String target) {
        this.id = UUID.randomUUID().toString();
        this.source = source;
        this.target = target;
    }

    public CytoscapeEdgeData(String source, String target, Boolean asymmetricConflict) {
        this(source, target);
        this.asymmetricConflict = asymmetricConflict;
    }

    public CytoscapeEdgeData(String source, String target, String startCardinality, String endCardinality) {
        this(source, target);
        this.startCardinality = startCardinality;
        this.endCardinality = endCardinality;
    }

    public CytoscapeEdgeData(String source, String target, Boolean asymmetricConflict, String startCardinality, String endCardinality) {
        this(source, target, startCardinality, endCardinality);
        this.asymmetricConflict = asymmetricConflict;
    }

    public CytoscapeEdgeData(String source, String target, String startCardinality, String endCardinality, String quantity) {
        this(source, target);
        this.startCardinality = startCardinality;
        this.endCardinality = endCardinality;
        this.quantity = quantity;
    }

    public CytoscapeEdgeData(String source, String target, Boolean asymmetricConflict, String startCardinality, String endCardinality, Integer quantity) {
        this(source, target, startCardinality, endCardinality, String.valueOf(quantity));
        this.asymmetricConflict = asymmetricConflict;
    }
}
