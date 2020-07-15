package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;

@Data
@NodeEntity("ProcessInstance")
public class ProcessInstance {
}
