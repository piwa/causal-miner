package at.ac.wuwien.causalminer.frontend.model.cytoscape;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CytoscapeNode {

    private CytoscapeNodeData data;

    public CytoscapeNode(CytoscapeNodeData data) {
        this.data = data;
    }
}
