package at.ac.wuwien.causalminer.neo4jdb.domain.query.results;

import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
@Data
public class CardinalityQueryResult {

    private String startNodeType;
    private String endNodeType;
    private Integer maxCardinality;

}
