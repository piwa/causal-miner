package at.ac.wuwien.causalminer.neo4jdb.domain.query.results;

import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Data
public class StartCardinalityViolationsQueryResult {

    private String instanceId;
    private Long targetNodeId;
    private List<Long> sourceNodeIds;
    private String sourceNodeType;
    private String targetNodeType;
    private Integer cardinality;
}
