package at.ac.wuwien.causalminer.neo4jdb.repositories;

import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelStartActivity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface ModelStartActivityRepository extends Neo4jRepository<ModelStartActivity, Long> {

    @Query("MATCH (n:Individual:ModelStartActivity) RETURN n")
    List<ModelStartActivity> findAllIndividualProcessModels();

    @Query("MATCH (n:Combined:ModelStartActivity) RETURN n")
    List<ModelStartActivity> findAllCombinedProcessModels();

}

