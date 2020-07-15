package at.ac.wuwien.causalminer.neo4jdb.repositories;

import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface InstanceStartActivityRepository extends Neo4jRepository<InstanceStartActivity, Long> {

    @Query("MATCH (n:InstanceStartActivity)-[:INSTANCE_OF]->(:ModelStartActivity {modelId: $modelId}) return n")
    List<InstanceStartActivity> findByModelId(@Param("modelId") String modelId);

    @Query("MATCH (n:InstanceStartActivity) WHERE n.instanceId IN $instanceIds RETURN n")
    List<InstanceStartActivity> findAllByInstanceIdIsIn(Collection<String> instanceIds);
}

