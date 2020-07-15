package at.ac.wuwien.causalminer.frontend.model.cytoscape;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CytoscapeElement {

    private List<CytoscapeNode> nodes = new ArrayList<>();
    private List<CytoscapeEdge> edges = new ArrayList<>();

}
