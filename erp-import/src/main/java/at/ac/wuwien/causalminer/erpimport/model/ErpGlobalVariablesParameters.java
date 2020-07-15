package at.ac.wuwien.causalminer.erpimport.model;

import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceIntermediateActivity;
import at.ac.wuwien.causalminer.erpimport.NodeCache;
import at.ac.wuwien.causalminer.transformer.model.EventBuilder;
import at.ac.wuwien.causalminer.transformer.model.INode;
import at.ac.wuwien.causalminer.transformer.model.Node;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Set;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class ErpGlobalVariablesParameters implements ErpData {

    protected DateTime creationDate;
    protected String creationUser;
    protected DateTime changeDate;
    protected String changeUser;

    protected Node getCurrentNode(INode parentNode, UUID instanceId, String label, String eventType, ErpId id, EventBuilder eventBuilder) {
        if (instanceId == null) {
            instanceId = UUID.randomUUID();
        }

        Node currentNode;
        if (NodeCache.nodes.containsKey(id)) {
            currentNode = (Node)NodeCache.nodes.get(id);
        } else {
            currentNode = new Node(label, eventType, creationDate, changeDate, eventBuilder);
            NodeCache.nodes.put(id, currentNode);
        }

        currentNode.addInstanceId(instanceId);
        currentNode.addParent(parentNode);

        return currentNode;
    }

    @Transient
    protected abstract InstanceIntermediateActivity getEvent();



    public InstanceActivity getEvent(Set<UUID> instanceIds, Boolean changeDateNullUseCreationDate) {
        InstanceIntermediateActivity event = getEvent();
        event.addInstanceIds(instanceIds);

        event.getOrigIds().putAll(getId().getEventIds());

        if(this.getCreationDate() == null) {
            event.getOrigParameters().put("creationDate", "null");
        } else {
            event.getOrigParameters().put("creationDate", this.getCreationDate().toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        }
        event.getOrigParameters().put("creationUser", this.getCreationUser());

        DateTime tempChangeDate = this.getChangeDate();
        if(changeDateNullUseCreationDate && tempChangeDate == null) {
            tempChangeDate = this.getCreationDate();
        }
        if (this.getChangeDate() != null) {
            event.getOrigParameters().put("changeDate", this.getChangeDate().toString());
            event.getOrigParameters().put("changeUser", this.getChangeUser());

            Duration duration = new Duration(this.getCreationDate(), tempChangeDate);
            event.getOrigParameters().put("duration", String.valueOf(duration.getMillis()));
        }

        event.setDuration(new Duration(this.getCreationDate(), tempChangeDate).getMillis());

        return event;
    }

}
