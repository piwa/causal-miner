package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.Map;
import java.util.Optional;

@Data
@RelationshipEntity(type = "INSTANCE_FOLLOW_UP_AGGREGATED")
public class InstanceRelationshipAggregated extends InstanceRelationship {

    @Property
    private Double probability;

    @Property(name = "count_*")
    private Integer count;

    public InstanceRelationshipAggregated(InstanceActivity startEvent, InstanceActivity endEvent) {
        super(startEvent, endEvent);
    }

    public Map<String, String> getProperties() {
        Map<String, String> properties = super.getProperties();

        properties.put("Probability", Optional.ofNullable(probability).orElse(0.0).toString());
        properties.put("Count", Optional.ofNullable(count).orElse(0).toString());

        return properties;
    }

    public String getLabel() {
        return String.valueOf(count);
    }

    @Override
    public Boolean isAggregated() {
        return true;
    }

    @Override
    public Integer getCount() {
        return count;
    }

}
