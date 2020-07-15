package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Properties;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("InstanceStartActivity")
public class InstanceStartActivity extends AbstractInstanceStartEndActivity {

    @Properties
    private Map<String, String> origIds = new HashMap<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "INSTANCE_OF")
    private List<OfRelationship> instanceOfRelationships = new ArrayList<>();

    public InstanceStartActivity() {
        super("Start");
    }
}
