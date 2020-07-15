package at.ac.wuwien.causalminer.transformer.model;

import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceEndActivity;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.*;

@Getter
public class EndNode extends AbstractNode implements INode, EventBuilder {

    private UUID id;

    private String label = "End";

    private UUID instanceId;

    private Set<INode> parents = new HashSet<>();

    private LinkedHashSet<UUID> branchIds = new LinkedHashSet<>();

    public static EndNode of(UUID instanceId) {
        return new EndNode(instanceId);
    }

    private EndNode(UUID instanceId) {
        this.id = UUID.randomUUID();
        this.instanceId = instanceId;
    }

    @Override
    public InstanceActivity getEvent(Set<UUID> instanceIds, Boolean changeDateNullUseCreationDate) {

        Optional<DateTime> latestCreationTime = parents.stream().map(INode::getActivityChangeDate).filter(Objects::nonNull).max(DateTime::compareTo);

        Iterator<UUID> iterator = instanceIds.iterator();
        if(iterator.hasNext()) {
            UUID instanceId = iterator.next();
            InstanceEndActivity endActivity = new InstanceEndActivity();
            latestCreationTime.ifPresent(endActivity::setTimestamp);
            endActivity.setInstanceId(instanceId.toString());
            return endActivity;
        }
        return null;    // TODO throw exception
    }


    @Override
    public Set<UUID> getInstanceIds() {
        Set<UUID> set = new HashSet<>();
        set.add(instanceId);
        return set;
    }

    @Override
    public Set<INode> getChildren() {
        return new HashSet<>();
    }

    @Override
    public DateTime getActivityCreationTime() {
        return parents.stream().map(INode::getActivityCreationTime).max(DateTime::compareTo).get();        // TODO get not checked
    }

    @Override
    public DateTime getActivityChangeDate() {
        return parents.stream().map(INode::getActivityCreationTime).max(DateTime::compareTo).get();        // TODO get not checked
    }

    @Override
    public EventBuilder getEventBuilder() {
        return this;
    }

    @Override
    public boolean isEndNode() {
        return true;
    }

    @Override
    public String getEventType() {
        return "End";
    }

}
