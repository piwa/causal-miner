package at.ac.wuwien.causalminer.neo4jdb.services;

import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface TestRepository extends Neo4jRepository<InstanceStartActivity, Long> {

    @Query("MATCH (n) DETACH DELETE n;")
    void emptyDB();

    @Query("CALL apoc.cypher.runFile($file)")
    void callApocRunFile(@Param("file") String file);
}
