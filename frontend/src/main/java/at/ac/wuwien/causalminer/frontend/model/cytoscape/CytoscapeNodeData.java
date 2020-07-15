package at.ac.wuwien.causalminer.frontend.model.cytoscape;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CytoscapeNodeData {

    private String id;
    private String label;

    private String nodeType;
    private String color = "#91c4cc";
    private String display = "element";
    private String shape = "round-rectangle";

    private String borderColor = "#000000";
    private Integer borderWidth = 1;

    private List<String> labels = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();

    public CytoscapeNodeData(String id, String label, String nodeType) {
        this.label = label;
        this.nodeType = nodeType;

        this.id = id;
    }

    public void addAllProperties(Map<String, String> origParameters) {
        this.properties.putAll(origParameters);
    }

    public void setNodeType(String nodeType) {
        if(nodeType.contains("NULL")) {
            this.nodeType = "NULL";
        }
        else {
            this.nodeType = nodeType;
        }
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public void addLabels(List<String> labels) {
        labels.addAll(labels);
    }
}
