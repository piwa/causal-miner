package at.ac.wuwien.causalminer.neo4jdb.domain.model;

import at.ac.wuwien.causalminer.neo4jdb.domain.INode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.*;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity(value = "ModelActivity")
public class ModelActivity extends ProcessModel implements INode<ModelRelationship> {

    @Id
    @GeneratedValue
    private Long id;

    @Labels
    private List<String> labels = new ArrayList<>();

    @Property(name = "avg_duration")
    private Double durationAvg;

    @Property(name = "min_duration")
    private Long durationMin;

    @Property(name = "max_duration")
    private Double durationMax;

    @Property(name = "count_*")
    private Integer count;

    @Property
    private String type;

    @Property
    private List<Long> origNodeIds;           // TODO create an own AggregateInstanceActivity
    @Property
    private List<String> origNodeInstanceIds;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "MODEL_FOLLOW_UP")
    private List<ModelRelationship> followUpRelationships = new ArrayList<>();


    public ModelActivity() {
    }

    public ModelActivity(String type) {
        this.labels.add(type);
        this.type = type;
    }

    public ModelActivity(ModelActivity modelActivity) {
        this.id = modelActivity.getId();
        this.labels = modelActivity.getLabels();
        this.durationAvg = modelActivity.getDurationAvg();
        this.durationMin = modelActivity.getDurationMin();
        this.durationMax = modelActivity.getDurationMax();
        this.count = modelActivity.getCount();
        this.type = modelActivity.getType();
        this.followUpRelationships = modelActivity.getFollowUpRelationships();
    }

    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();

        properties.put("Min Duration", Optional.ofNullable(durationMin).orElse(0L).toString());
        properties.put("Max Duration", Optional.ofNullable(durationMax).orElse(0.0).toString());
        properties.put("Avg Duration", Optional.ofNullable(durationAvg).orElse(0.0).toString());
        properties.put("Count", Optional.ofNullable(count).orElse(0).toString());

        return properties;
    }

    @Override
    public boolean isEssentialForProcessDepiction() {
        return true;
    }

    @Override
    public boolean isNullNode() {
        return false;
    }

    @Override
    public boolean isAggregated() {
        return true;
    }


}
