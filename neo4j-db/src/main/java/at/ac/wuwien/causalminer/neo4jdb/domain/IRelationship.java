package at.ac.wuwien.causalminer.neo4jdb.domain;

import java.util.Map;

public interface IRelationship<T extends INode<?>, V extends INode<?>> {
    Long getId();

    T getStartEvent();

    V getEndEvent();

    void setStartEvent(T startEvent);

    void setEndEvent(V endEvent);

    Map<String, String> getProperties();

    String getLabel();

    Boolean isAsymmetricConflictRelationship();

    Boolean isAggregated();

    Integer getCount();
}
