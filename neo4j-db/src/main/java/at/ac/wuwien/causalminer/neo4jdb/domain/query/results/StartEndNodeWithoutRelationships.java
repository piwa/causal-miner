package at.ac.wuwien.causalminer.neo4jdb.domain.query.results;

import org.joda.time.DateTime;

import java.util.Map;

public interface StartEndNodeWithoutRelationships {
    Long getId();

    java.util.List<String> getLabels();

    String getType();

    void setId(Long id);

    void setLabels(java.util.List<String> labels);

    void setType(String type);

    Map<String, String> getProperties();

    String getInstanceId();

    DateTime getTimestamp();

    void setInstanceId(String instanceId);

    void setTimestamp(DateTime timestamp);
}
