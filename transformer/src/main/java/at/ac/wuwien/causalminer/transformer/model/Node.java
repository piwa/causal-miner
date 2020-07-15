package at.ac.wuwien.causalminer.transformer.model;

import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

import java.util.*;

@Getter
public class Node extends AbstractNode implements INode {

    private UUID id;

    private String label;

    private Set<UUID> instanceIds = new HashSet<>();

    private Set<INode> parents = new HashSet<>();

    private Set<INode> children = new HashSet<>();

    private DateTime activityCreationTime;
    private DateTime activityChangeDate;

    @ToString.Exclude
    private EventBuilder eventBuilder;

    private String eventType;

    public Node(UUID instanceId, String eventType, DateTime activityCreationTime, DateTime activityChangeDate, EventBuilder eventBuilder) {
        this.id = UUID.randomUUID();
        this.addInstanceId(instanceId);
        this.activityCreationTime = activityCreationTime;
        this.activityChangeDate = activityChangeDate;
        this.eventBuilder = eventBuilder;
        this.eventType = eventType;
    }

    public Node(UUID instanceId, String label, String eventType, DateTime activityCreationTime, DateTime activityChangeDate, EventBuilder eventBuilder) {
        this(instanceId, eventType, activityCreationTime, activityChangeDate, eventBuilder);
        this.label = label;
    }

    public Node(String label, String eventType, DateTime activityCreationTime, DateTime activityChangeDate, EventBuilder eventBuilder) {
        this.id = UUID.randomUUID();
        this.label = label;
        this.activityCreationTime = activityCreationTime;
        this.activityChangeDate = activityChangeDate;
        this.eventBuilder = eventBuilder;
        this.eventType = eventType;
    }

    public void addInstanceId(UUID instanceId) {
        if(instanceId != null) {
            instanceIds.add(instanceId);
        }
    }

    public void addParents(Set<INode> nodes) {
        nodes.forEach(this::addParent);
    }

    public void addParent(INode node) {
        if(node != null) {
            parents.add(node);
        }
    }

    public void addChildren(Set<INode> nodes) {
        nodes.forEach(this::addChild);
    }

    public void addChild(INode node) {
        if(node != null) {
            children.add(node);
        }
    }

    @Override
    public boolean isEndNode() {
        return false;
    }


}
