package at.ac.wuwien.causalminer.transformer.model;



import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;

import java.util.Set;
import java.util.UUID;

public interface EventBuilder {

    InstanceActivity getEvent(Set<UUID> instanceIds, Boolean changeDateNullUseCreationDate);
}
