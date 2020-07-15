package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("InstanceEndActivity")
public class InstanceEndActivity extends AbstractInstanceStartEndActivity {

    public InstanceEndActivity() {
        super("End");
    }
}
