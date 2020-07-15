package at.ac.wuwien.causalminer.neo4jdb.domain.model;

import at.ac.wuwien.causalminer.neo4jdb.domain.IHomogeneousRelationship;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@RelationshipEntity(type = "MODEL_FOLLOW_UP")
public class ModelRelationship implements IHomogeneousRelationship<ModelActivity> {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private Double probability;

    @Property(name = "count_*")
    private Integer count;

    @Property
    private Integer startCardinality;

    @Property
    private Integer endCardinality;

    @Property
    private Boolean asymmetricConflictRelationship = false;

    @StartNode
    private ModelActivity startEvent;

    @EndNode
    private ModelActivity endEvent;

    public ModelRelationship(ModelActivity startEvent, ModelActivity endEvent) {
        this.startEvent = startEvent;
        this.endEvent = endEvent;
    }

    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();

        properties.put("Probability", Optional.ofNullable(probability).orElse(0.0).toString());
        properties.put("Count", Optional.ofNullable(count).orElse(1).toString());
        properties.put("Start Cardinality", Optional.ofNullable(startCardinality).orElse(1).toString());
        properties.put("End Cardinality", Optional.ofNullable(endCardinality).orElse(1).toString());

        return properties;
    }

    public String getLabel() {

        if(startCardinality != null && endCardinality != null) {
            String startCardinalityString = String.valueOf(startCardinality);
            if(startCardinality == Integer.MAX_VALUE) {
                startCardinalityString = "N";
            }
            String endCardinalityString = String.valueOf(endCardinality);
            if(endCardinality == Integer.MAX_VALUE) {
                endCardinalityString = "N";
            }

            return startCardinalityString + " : " + endCardinalityString;
        }
        else if(count != null) {
            return String.valueOf(count);
        }
        return "";
    }

    public Boolean isAsymmetricConflictRelationship() {
        return asymmetricConflictRelationship;
    }

    @Override
    public Boolean isAggregated() {
        return true;
    }
}
