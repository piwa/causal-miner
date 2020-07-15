package at.ac.wuwien.causalminer.neo4jdb.domain.query.results;

import at.ac.wuwien.causalminer.neo4jdb.domain.UuidConverter;
import lombok.Data;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.UUID;

@QueryResult
@Data
public class InstanceBasedEndCardinalityQueryResult {

    @Convert(UuidConverter.class)
    private UUID currentInstanceId;
    private Long startNodeId;
    private String startNodeType;
    private List<Long> endNodeIds;
    private String endNodeType;
    private Integer cardinality;

}
