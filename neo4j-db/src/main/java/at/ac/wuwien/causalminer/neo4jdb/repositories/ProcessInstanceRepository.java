package at.ac.wuwien.causalminer.neo4jdb.repositories;

import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.TotalDurationsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.InstancesWithSameProcessModelResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProcessInstanceRepository extends Neo4jRepository<InstanceActivity, Long> {
    @Query("MATCH (n:ProcessInstance) DETACH DELETE n")
    void deleteAll();

//    @Query("MATCH p=(n:InstanceStartActivity {instanceId: $instanceId})-[*]->(:InstanceEndActivity {instanceId: $instanceId}) RETURN p " +
//            "UNION " +
//            "MATCH p=(n:InstanceStartActivity {instanceId: $instanceId})-[*]->(:InstanceNullActivity) RETURN p")
    @Query("MATCH p=(n:InstanceStartActivity {instanceId: $instanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: $instanceId}) RETURN p ")
    List<InstanceActivity> findByInstanceId(@Param("instanceId") String instanceId);

    @Query("MATCH p=(n:InstanceStartActivity {instanceId: $instanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: $instanceId}) RETURN p ")
    List<InstanceActivity> findByInstanceId(@Param("instanceId") String instanceId, PageRequest pageable);

    @Query("MATCH p=(n:InstanceStartActivity)-[*]->(m:InstanceEndActivity) return p")
    List<InstanceActivity> findAllInstances();

    @Query( "MATCH p=(n:InstanceStartActivity)-[*]->(m:InstanceEndActivity {instanceId: n.instanceId}) " +
            "UNWIND nodes(p) as node " +
            "WITH node, n AS startNode, m AS endNode " +
            "WHERE NOT (node:InstanceStartActivity) AND NOT (node:InstanceEndActivity) " +
            "WITH startNode, endNode, collect(distinct node) AS nodes " +
            "RETURN startNode, endNode, reduce(total = 0, node IN nodes | total + node.duration) AS totalDuration " +
            "ORDER BY totalDuration DESC")
    List<TotalDurationsQueryResult> getTotalDurationsFromStartToEnd();

    @Query("MATCH p = (modelAct:ProcessModel:Individual:ModelStartActivity)-[*]->(:ProcessModel:Individual:ModelEndActivity {modelId: modelAct.modelId}) " +
            "MATCH (instanceAct:ProcessInstance:InstanceStartActivity)-[:INSTANCE_OF]->(modelAct) " +
            "WITH [x IN NODES(p) | x.type] AS labelPath, {instanceId: instanceAct.instanceId, timestamp: instanceAct.timestamp} AS activity " +
            "ORDER BY labelPath " +
            "WITH activity, collect(labelPath) AS combinedProcessModel " +
            "WITH combinedProcessModel, collect(activity) AS similarInstanceModels " +
            "RETURN similarInstanceModels")
    List<InstancesWithSameProcessModelResult> findAllInstancesWithSameProcessModel();

}

