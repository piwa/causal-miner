package at.ac.wuwien.causalminer.frontend.model.cytoscape;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CytoscapeEdge {

    private CytoscapeEdgeData data;

    public CytoscapeEdge(CytoscapeEdgeData data) {
        this.data = data;
    }

    @JsonProperty("classes")
    public List<String> getClasses() {
        List<String> classes = new ArrayList<>();
        classes.add("top-center");

        if(data != null && data.getAsymmetricConflict()) {
            classes.add("asymmetricEdge");
        }
        return classes;
    }
}
