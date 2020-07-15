package at.ac.wuwien.causalminer.neo4jdb.domain.query.results;

import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelRelationship;
import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Data
public class AggregateNeighborsQueryResult {

    private List<ModelActivity> modelActivityList;
    private List<ModelRelationship> modelRelationshipList;

}
