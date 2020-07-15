package at.ac.wuwien.causalminer.neo4jdb.domain.query.results;

import at.ac.wuwien.causalminer.neo4jdb.domain.DateTimeConverter;
import lombok.Data;
import org.joda.time.DateTime;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Data
public class InstancesWithSameProcessModelResult {

    private List<SimilarInstanceModels> similarInstanceModels;

    @Data
    public class SimilarInstanceModels {

        private String instanceId;

        @Convert(DateTimeConverter.class)
        private DateTime timestamp;

    }
}
