package at.ac.wuwien.causalminer.neo4jdb.domain.query.results;

import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
@Data
public class EventTypeResult {

    private final String origId;
    private final Long connectedInstanceAmount;

}
