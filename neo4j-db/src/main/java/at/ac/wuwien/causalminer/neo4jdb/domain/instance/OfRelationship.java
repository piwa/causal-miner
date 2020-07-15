package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import at.ac.wuwien.causalminer.neo4jdb.domain.IRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelActivity;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Data
@RelationshipEntity(type = "INSTANCE_OF")
public class OfRelationship implements IRelationship<InstanceActivity, ModelActivity> {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private InstanceActivity startEvent;

    @EndNode
    private ModelActivity endEvent;

    public OfRelationship(InstanceActivity startEvent, ModelActivity endEvent) {
        this.startEvent = startEvent;
        this.endEvent = endEvent;
    }

    public Map<String, String> getProperties() {
        return new HashMap<>();
    }

    public String getLabel() {
        return "";
    }

    @Override
    public Boolean isAsymmetricConflictRelationship() {
        return false;
    }

    @Override
    public Boolean isAggregated() {
        return false;
    }

    @Override
    public Integer getCount() {
        return null;
    }
}
