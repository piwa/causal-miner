package at.ac.wuwien.causalminer.transformer.model;

import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceNullActivity;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.*;

@Getter
public class NullNode extends AbstractNode implements INode, EventBuilder {

    private final UUID id = UUID.randomUUID();

    private String label;

    private String eventType;

    private UUID instanceId;

    private Set<INode> parents = new HashSet<>();

    private Set<INode> children = new HashSet<>();

    private LinkedHashSet<UUID> branchIds = new LinkedHashSet<>();

    public static NullNode of(Node parentNode, UUID instanceId, String type) {
        return new NullNode(parentNode, instanceId, type);
    }

    private NullNode(Node parentNode, UUID instanceId, String label) {

        this.parents.add(parentNode);
        this.label = "NULL_" + label;
        this.instanceId = instanceId;
        this.eventType = label;
    }

    @Override
    public InstanceActivity getEvent(Set<UUID> instanceIds, Boolean changeDateNullUseCreationDate) {
        InstanceNullActivity event = new InstanceNullActivity();
        event.setType(eventType);
        event.addInstanceIds(instanceIds);
        return event;
    }

    @Override
    public Set<UUID> getInstanceIds() {
        Set<UUID> set = new HashSet<>();
        set.add(instanceId);
        return set;
    }

    @Override
    public Set<INode> getParents() {
//        Set<INode> nodes = new HashSet<>();
//        nodes.add(parent);
//        return nodes;
        return parents;
    }

    @Override
    public Set<INode> getChildren() {
        return children;
    }

    @Override
    public DateTime getActivityCreationTime() {
        return null;
    }

    @Override
    public DateTime getActivityChangeDate() {
        return null;
    }

    @Override
    public EventBuilder getEventBuilder() {
        return this;
    }

    @Override
    public boolean isEndNode() {
        return false;
    }

}
