package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("InstanceNullActivity")
public class InstanceNullActivity extends InstanceActivity {

    @Property
    private List<String> instanceIds = new ArrayList<>();

    @Property
    private Boolean nullInstance = true;

    @Override
    public Map<String, String> getProperties() {
        return new HashMap<>();
    }

    public void addInstanceIds(Set<UUID> instanceIds) {
        instanceIds.forEach(uuid -> this.instanceIds.add(uuid.toString()));
    }

    @Override
    public boolean isEssentialForProcessDepiction() {
        return true;
    }

    @Override
    public boolean isNullNode() {
        return true;
    }

}
