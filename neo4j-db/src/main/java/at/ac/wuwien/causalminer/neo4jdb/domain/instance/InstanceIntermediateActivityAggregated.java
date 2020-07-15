package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("InstanceIntermediateActivityAggregated")
public class InstanceIntermediateActivityAggregated extends InstanceIntermediateActivity {

    @Property
    private List<String> instanceIds = new ArrayList<>();

    @Property(name = "avg_duration")
    private Double durationAvg;

    @Property(name = "min_duration")
    private Long durationMin;

    @Property(name = "max_duration")
    private Long durationMax;

    @Property(name = "count_*")
    private Integer count;

    @Property
    private String type;

    public InstanceIntermediateActivityAggregated() {
    }

    public InstanceIntermediateActivityAggregated(String type) {
        super(type);
    }

    public static InstanceIntermediateActivityAggregated of(InstanceActivity firstEndActivity) {
        InstanceIntermediateActivityAggregated instanceActivity = new InstanceIntermediateActivityAggregated(firstEndActivity.getType());
        instanceActivity.setLabels(firstEndActivity.getLabels());
        instanceActivity.setType(firstEndActivity.getType());
        if(firstEndActivity instanceof InstanceIntermediateActivity) {
            instanceActivity.setInstanceIds(((InstanceIntermediateActivity) firstEndActivity).getInstanceIds());
        }
        firstEndActivity.getFollowUpRelationships().forEach(rel -> {
            InstanceRelationshipAggregated instanceRelationship = new InstanceRelationshipAggregated(instanceActivity, rel.getEndEvent());
            instanceActivity.getFollowUpRelationships().add(instanceRelationship);
        });

        return instanceActivity;
    }

    @Override
    public Map<String, String> getProperties() {

        Map<String, String> properties = super.getProperties();

        StringBuilder stringBuilder = new StringBuilder("[");
        instanceIds.forEach(id -> stringBuilder.append(id).append(","));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
        properties.put("Cases", stringBuilder.toString());

        properties.put("Min Duration", Optional.ofNullable(durationMin).orElse(0L).toString());
        properties.put("Max Duration", Optional.ofNullable(durationMax).orElse(0L).toString());
        properties.put("Avg Duration", Optional.ofNullable(durationAvg).orElse(0.0).toString());
        properties.put("Count", Optional.ofNullable(count).orElse(0).toString());

        return properties;
    }

    public void addInstanceIds(Set<UUID> instanceIds) {
        instanceIds.forEach(uuid -> this.instanceIds.add(uuid.toString()));
    }

    @Override
    public boolean isEssentialForProcessDepiction() {
        return true;
    }
}
