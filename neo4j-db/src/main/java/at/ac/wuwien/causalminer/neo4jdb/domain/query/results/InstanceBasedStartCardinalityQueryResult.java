package at.ac.wuwien.causalminer.neo4jdb.domain.query.results;

import at.ac.wuwien.causalminer.neo4jdb.domain.UuidConverter;
import lombok.Data;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.UUID;

@QueryResult
@Data
public class InstanceBasedStartCardinalityQueryResult {

    @Convert(UuidConverter.class)
    private UUID currentInstanceId;
    private Long endNodeId;
    private String endNodeType;
    private List<Long> startNodeIds;
    private String startNodeType;
    private Integer cardinality;

}
