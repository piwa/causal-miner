package at.ac.wuwien.causalminer.transformer.model;

import java.util.UUID;

public interface INode {

    UUID getId();

    String getLabel();

    String getEventType();

    java.util.Set<java.util.UUID> getInstanceIds();

    java.util.Set<INode> getParents();

    java.util.Set<INode> getChildren();

    org.joda.time.DateTime getActivityCreationTime();

    org.joda.time.DateTime getActivityChangeDate();

    EventBuilder getEventBuilder();

    boolean isEndNode();

}
