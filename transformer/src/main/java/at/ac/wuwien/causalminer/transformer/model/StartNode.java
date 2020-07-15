package at.ac.wuwien.causalminer.transformer.model;

import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.*;

@Getter
public class StartNode extends AbstractNode implements INode, EventBuilder {

    private UUID id;

    private String label = "Start";

    private UUID instanceId;

    private Set<INode> children = new HashSet<>();

    private Map<String, String> originalIds = new HashMap<>();

    public StartNode(UUID instanceId, Map<String, String> originalIds) {
        this.id = UUID.randomUUID();
        this.instanceId = instanceId;
        this.originalIds = originalIds;
    }

    @Override
    public InstanceActivity getEvent(Set<UUID> instanceIds, Boolean changeDateNullUseCreationDate) {

        Optional<DateTime> earliestCreationTime = children.stream().map(INode::getActivityCreationTime).min(DateTime::compareTo);

        Iterator<UUID> iterator = instanceIds.iterator();
        if(iterator.hasNext()) {
            UUID instanceId = iterator.next();
            InstanceStartActivity startActivity = new InstanceStartActivity();
            earliestCreationTime.ifPresent(startActivity::setTimestamp);
            startActivity.setInstanceId(instanceId.toString());
            startActivity.setOrigIds(originalIds);
            return startActivity;
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
    public Set<INode> getParents() {
        return new HashSet<>();
    }

    @Override
    public DateTime getActivityCreationTime() {
        return children.stream().map(INode::getActivityCreationTime).min(DateTime::compareTo).get();        // TODO get not chocked
    }

    @Override
    public DateTime getActivityChangeDate() {
        return children.stream().map(INode::getActivityCreationTime).min(DateTime::compareTo).get();        // TODO get not chocked
    }

    @Override
    public EventBuilder getEventBuilder() {
        return this;
    }

    @Override
    public boolean isEndNode() {
        return false;
    }

    @Override
    public String getEventType() {
        return "Start";
    }
}
