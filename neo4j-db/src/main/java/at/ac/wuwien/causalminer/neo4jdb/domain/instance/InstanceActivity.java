package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import at.ac.wuwien.causalminer.neo4jdb.domain.INode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.*;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("InstanceActivity")
public abstract class InstanceActivity extends ProcessInstance implements INode<InstanceRelationship> {

    @Id
    @GeneratedValue
    private Long id;

    @Labels
    private List<String> labels = new ArrayList<>();

    @Property
    private String type;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "INSTANCE_FOLLOW_UP")
    private List<InstanceRelationship> followUpRelationships = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "NULL_INSTANCE_FOLLOW_UP")
    private List<NullInstanceRelationship> nullFollowUpRelationships = new ArrayList<>();

    public InstanceActivity() {
    }

    public InstanceActivity(String type) {
        this.type = type;
//        this.labels.add(type);
    }

    @Override
    public Map<String, String> getProperties() {

        Map<String, String> properties = new HashMap<>();
        properties.put("erpTableName", Optional.ofNullable(getType()).orElse("-"));

        return properties;
    }

    @Override
    public boolean isAggregated() {
        return false;
    }

    @Override
    public Integer getCount() {
        return null;
    }
}
