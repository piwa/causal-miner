package at.ac.wuwien.causalminer.neo4jdb.domain;

import java.util.Map;

public interface INode<T extends IHomogeneousRelationship<?>> {
    Long getId();

    java.util.List<String> getLabels();

    String getType();

    java.util.List<T> getFollowUpRelationships();

    void setId(Long id);

    void setLabels(java.util.List<String> labels);

    void setType(String type);

    Map<String, String> getProperties();

    void setFollowUpRelationships(java.util.List<T> followUpRelationships);

    boolean isEssentialForProcessDepiction();

    boolean isNullNode();

    boolean isAggregated();

    Integer getCount();
}
