package at.ac.wuwien.causalminer.neo4jdb.repositories;

import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.AggregateNeighborsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.CombinableIndividualModelsResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.ModelCheckerResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProcessModelRepository extends Neo4jRepository<ModelActivity, Long> {

    @Query("MATCH (n:ProcessModel) DETACH DELETE n")
    void deleteAll();

    /* ----------- Global Process Models ----------- */
    @Query("MATCH p=(:ProcessModel:Global:ModelStartActivity)-[*]->(:ProcessModel:Global:ModelEndActivity) RETURN p " +
            "UNION " +
            "MATCH p=(:ProcessModel:Global:ModelStartActivity)-[*]->(:NULL) RETURN p")
//    @Query( "CALL at.ac.wuwien.causalminer.neo4jPlugin.extendedGroupAllNodes(['ProcessModel', 'Global', 'ModelActivity'], {Start:'ModelStartActivity', End:'ModelEndActivity'}, 'ModelIntermediateActivity', 'MODEL_FOLLOW_UP', ['InstanceActivity'], ['type'], [{`*`:'count', duration:['min', 'max', 'avg']}, {`*`:'count'} ]) YIELD node, relationship " +
//            "RETURN collect(distinct node) AS modelActivityList, collect(distinct relationship) AS modelRelationshipList")
    List<ModelActivity> findGlobalProcessModel();

    @Query( "CALL at.ac.wuwien.causalminer.neo4jPlugin.extendedGroupAllNodes(['ProcessModel', 'Global', 'ModelActivity'], {Start:'ModelStartActivity', End:'ModelEndActivity'}, 'ModelIntermediateActivity', 'MODEL_FOLLOW_UP', ['InstanceStartActivity','InstanceEndActivity','InstanceIntermediateActivity'], ['type'], [{`*`:'count', duration:['min', 'max', 'avg']}, {`*`:'count'} ]) YIELD node, relationship " +
            "RETURN collect(distinct node) AS modelActivityList, collect(distinct relationship) AS modelRelationshipList")
    AggregateNeighborsQueryResult createGlobalProcessModel();

    @Query("CALL at.ac.wuwien.causalminer.neo4jPlugin.extendedGroupAllNodes(['ProcessModel', 'Global', 'ModelActivity'], {Start:'ModelStartActivity', End:'ModelEndActivity'}, 'ModelNullActivity', 'MODEL_FOLLOW_UP', ['InstanceNullActivity'], ['type'], [{`*`:'count', duration:['min', 'max', 'avg']}, {`*`:'count'} ]) YIELD node, relationship " +
            "RETURN collect(distinct node) AS modelActivityList, collect(distinct relationship) AS modelRelationshipList")
    AggregateNeighborsQueryResult createGlobalNullActivitiesProcessModel();


    /* ----------- Combined Process Models ----------- */
    @Query("MATCH p=(n:InstanceStartActivity {instanceId: n.instanceId})-[:INSTANCE_FOLLOW_UP*..10]->(m:InstanceEndActivity {instanceId: n.instanceId})  " +
            "WITH dateTime(n.timestamp) AS time, p  " +
            "WHERE time > datetime($startTime) AND time < datetime($endTime)  " +
            "UNWIND nodes(p) AS unwindedNodes  " +
            "WITH collect(distinct unwindedNodes) AS collectedNodes  " +
            "CALL at.ac.wuwien.causalminer.neo4jPlugin.extendedGroup(collectedNodes, ['ProcessModel','Individual', 'ModelActivity'], {Start:'ModelStartActivity', End:'ModelEndActivity'}, 'ModelIntermediateActivity', 'MODEL_FOLLOW_UP', ['InstanceActivity'], ['type'], [{`*`:'count', duration:['min', 'max', 'avg']}, {`*`:'count'} ], {relConsiderOnlyGivenNodes:true}) YIELD node, relationship  " +
            "RETURN collect(distinct node) AS nodes, collect(distinct relationship) AS rels")
    List<ModelActivity> createProcessModelOfTimeFrame(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Query("MATCH p=(n:InstanceStartActivity {instanceId: n.instanceId})-[:INSTANCE_FOLLOW_UP*..10]->(m:InstanceEndActivity {instanceId: n.instanceId}) " +
            "WHERE n.instanceId in $instanceIds " +
            "UNWIND nodes(p) AS unwindedNodes " +
            "WITH collect(distinct unwindedNodes) AS collectedNodes " +
            "CALL at.ac.wuwien.causalminer.neo4jPlugin.extendedGroup(collectedNodes, ['ProcessModel','Combined', 'ModelActivity'], {Start:'ModelStartActivity', End:'ModelEndActivity'}, 'ModelIntermediateActivity', 'MODEL_FOLLOW_UP', ['InstanceActivity'], ['type'], [{`*`:'count', duration:['min', 'max', 'avg']}, {`*`:'count'} ], {relConsiderOnlyGivenNodes:true}) YIELD node, relationship\n" +
            "RETURN collect(distinct node) AS nodes, collect(distinct relationship) AS rels")
    List<ModelActivity> createCombinedProcessModelForInstanceIds(@Param("instanceIds") Collection<String> instanceIds);


    /* ----------- Individual Process Models ----------- */
    @Query("MATCH p=(n:InstanceStartActivity {instanceId: $instanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: $instanceId})  " +
            "UNWIND nodes(p) AS unwindedNodes with collect(distinct unwindedNodes) AS collectedNodes  " +
            "CALL at.ac.wuwien.causalminer.neo4jPlugin.extendedGroup(collectedNodes, ['ProcessModel', 'Individual', 'ModelActivity'], {Start:'ModelStartActivity', End:'ModelEndActivity'}, 'ModelIntermediateActivity', 'MODEL_FOLLOW_UP', ['InstanceActivity'], ['type'], [{`*`:'count', duration:['min', 'max', 'avg']}, {`*`:'count'} ], {relConsiderOnlyGivenNodes:true}) YIELD node, relationship   " +
            "RETURN collect(distinct node) AS nodes, collect(distinct relationship) AS rels")
    List<ModelActivity> createIndividualProcessModelForInstanceId(@Param("instanceId") String instanceId);

    @Query("MATCH p=(:Individual:ModelStartActivity {modelId: $modelId})-[*]->(:Individual:ModelEndActivity {modelId: $modelId}) RETURN p")
    List<ModelActivity> findIndividualProcessModelByModelId(@Param("modelId") String modelId);


    /* ----------- Aggregated Process Models ----------- */
    @Query("MATCH p=(:InstanceStartActivity)-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity) " +
            "UNWIND nodes(p) AS unwindedNodes with collect(distinct unwindedNodes) AS collectedNodes  " +
            "CALL at.ac.wuwien.causalminer.neo4jPlugin.extendedGroup(collectedNodes, ['ProcessModel', 'Individual', 'ModelActivity'], {Start:'ModelStartActivity', End:'ModelEndActivity'}, 'ModelIntermediateActivity', 'MODEL_FOLLOW_UP', ['InstanceActivity'], ['type'], [{`*`:'count', duration:['min', 'max', 'avg']}, {`*`:'count'} ], {relConsiderOnlyGivenNodes:true}) YIELD node, relationship   " +
            "RETURN collect(distinct node) AS nodes, collect(distinct relationship) AS rels")
    List<ModelActivity> createAggregatedProcessModel(@Param("eventType") String eventType, @Param("origId") String origId);


    /* ----------- General ----------- */
    @Query("MATCH p=(n:ModelStartActivity {modelId: $modelId})-[*]->(:ModelEndActivity {modelId: $modelId}) return p")
    List<ModelActivity> findByModelId(@Param("modelId") String modelId);

    @Query("MATCH p = (n:ProcessModel:Individual:ModelStartActivity {modelId: n.modelId})-[*]->(:ProcessModel:Individual:ModelEndActivity {modelId: n.modelId}) " +
            "WITH [x IN NODES(p) | x.type] AS labelPath, n.modelId AS modelIds " +
            "ORDER BY labelPath " +
            "WITH modelIds, collect(labelPath) AS combinedProcessModel " +
            "WITH combinedProcessModel, collect(modelIds) AS similarIndividualModels " +
            "WHERE size(similarIndividualModels) > 1 " +
            "RETURN similarIndividualModels")
    List<CombinableIndividualModelsResult> findAllCombinableIndividualModels();


    @Query("MATCH p = (n:ProcessModel:Individual:ModelStartActivity)-[*]->(:ProcessModel:Individual:ModelEndActivity {modelId: n.modelId}) " +
            "WITH [x IN NODES(p) | x.type] AS labelPath, n.modelId AS modelId " +
            "ORDER BY labelPath " +
            "WITH modelId, collect(labelPath) AS combinedProcessModel " +
            "WHERE NOT at.ac.wuwien.causalminer.neo4jPlugin.equalLabelPaths(combinedProcessModel, $checkingPaths) " +
            "MATCH (:Individual:ModelStartActivity {modelId: modelId})<-[:INSTANCE_OF]-(n:InstanceStartActivity) " +
            "RETURN distinct n.instanceId AS instanceId")
    List<ModelCheckerResult> findPartialNotMatchingProcessModelsUserFunction(@Param("checkingPaths") List<List<String>> checkingPaths);
}

