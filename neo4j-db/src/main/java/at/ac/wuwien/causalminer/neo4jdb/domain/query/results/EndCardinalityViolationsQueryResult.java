package at.ac.wuwien.causalminer.neo4jdb.domain.query.results;

import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Data
public class EndCardinalityViolationsQueryResult {

    private String instanceId;
    private Long sourceNodeId;
    private List<Long> targetNodeIds;
    private String sourceNodeType;
    private String targetNodeType;
    private Integer cardinality;

}
