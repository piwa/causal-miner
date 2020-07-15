package at.ac.wuwien.causalminer.neo4jdb.repositories;

import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.*;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.*;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AggregatedProcessInstanceRepository extends Neo4jRepository<InstanceActivity, Long> {

    @Query("MATCH (conveningNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
            "UNWIND conveningNode.instanceIds AS currentInstanceId " +
            "MATCH p=(:InstanceStartActivity {instanceId: currentInstanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: currentInstanceId}) " +
            "RETURN p")
    List<InstanceActivity> findByEvent(@Param("eventType") String eventType, @Param("origId") String origId);

    @Query("MATCH (conveningNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
            "UNWIND conveningNode.instanceIds AS currentInstanceId " +
            "MATCH p=(:InstanceStartActivity {instanceId: currentInstanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: currentInstanceId}) " +
            "UNWIND nodes(p) AS unwindedNodes WITH currentInstanceId, collect(distinct unwindedNodes) AS collectedNodes " +
            "CALL at.ac.wuwien.causalminer.neo4jPlugin.extendedGroup(collectedNodes, ['ProcessModel', 'ModelActivity', 'Individual'], {Start:'ModelStartActivity', End:'ModelEndActivity'}, 'ModelIntermediateActivity', 'MODEL_FOLLOW_UP', ['InstanceActivity'], ['type'], [{`*`:'count', duration:['min', 'max', 'avg']}, {`*`:'count'} ], {relConsiderOnlyGiveNodes:true, transferIdsFromOriginalNode:true, transferInstanceIdsFromOriginalNode:true}) YIELD node, relationship " +
            "RETURN collect(distinct node) AS modelActivityList, collect(distinct relationship) AS modelRelationshipList")
    AggregateNeighborsQueryResult findByEventAndAggregateNeighbors(@Param("eventType") String eventType, @Param("origId") String origId);

    @Query("MATCH (conveningNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
            "UNWIND conveningNode.instanceIds AS currentInstanceId " +
            "MATCH p=(:InstanceStartActivity {instanceId: currentInstanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: currentInstanceId}) " +
            "UNWIND nodes(p) AS unwindedNodes WITH collect(distinct unwindedNodes) AS collectedNodes  " +
            "CALL at.ac.wuwien.causalminer.neo4jPlugin.extendedGroup(collectedNodes, ['ProcessModel', 'ModelActivity', 'Individual'], {Start:'ModelStartActivity', End:'ModelEndActivity'}, 'ModelIntermediateActivity', 'MODEL_FOLLOW_UP', ['InstanceActivity'], ['type'], [{`*`:'count', duration:['min', 'max', 'avg']}, {`*`:'count'} ], {relConsiderOnlyGivenNodes:true, transferIdsFromOriginalNode:true, transferInstanceIdsFromOriginalNode:true}) YIELD node, relationship   " +
            "RETURN collect(distinct node) AS modelActivityList, collect(distinct relationship) AS modelRelationshipList")
    AggregateNeighborsQueryResult aggregateTotalBySpedEvent(@Param("eventType") String eventType, @Param("origId") String origId);

    @Query("MATCH (conveningNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
            "UNWIND conveningNode.instanceIds AS currentInstanceId " +
            "MATCH (n:InstanceNullActivity) " +
//            "WHERE currentInstanceId IN n.instanceIds " +
//            "WHERE currentInstanceId IN n.instanceIds AND NOT ((:InstanceNullActivity)-->(n) AND (n)-->(:InstanceNullActivity)) " +
            "WHERE currentInstanceId IN n.instanceIds AND NOT ((:InstanceNullActivity)-->(n)) " +
            "return currentInstanceId, collect(distinct n.type) AS nullActivityTypes")
    List<NullModelActivitiesQueryResult> getNullModelActivities(@Param("eventType") String eventType, @Param("origId") String origId);

    @Query("MATCH (eventNode:InstanceIntermediateActivity {type: $eventType}) " +
            "RETURN eventNode.`origIds.SpedNr` as origId, size(eventNode.instanceIds) as connectedInstanceAmount")
    List<EventTypeResult> findAmountOfConnectedInstances(@Param("eventType") String eventType);

    @Query("MATCH (eventNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
            "RETURN eventNode.`origIds.SpedNr` as origId, size(eventNode.instanceIds) as connectedInstanceAmount")
    List<EventTypeResult> findAmountOfConnectedInstances(@Param("eventType") String eventType, @Param("origId") String origId);


//    @Query("MATCH (conveningNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
//            "UNWIND conveningNode.instanceIds AS currentInstanceId " +
//            "MATCH p=(:InstanceStartActivity {instanceId: currentInstanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: currentInstanceId}) " +
//            "UNWIND relationships(p) AS rel " +
//            "WITH currentInstanceId, startNode(rel) AS start, endNode(rel) AS end " +
//            "WITH currentInstanceId, start, start.type AS startNodeType, end.type AS endNodeType, collect(distinct end) AS collectedEnd " +
//            "RETURN currentInstanceId, ID(start) AS startNodeId, startNodeType, [x IN collectedEnd | ID(x)] AS endNodeIds, endNodeType, size(collectedEnd) AS cardinality")
    @Query("MATCH (conveningNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
        "UNWIND conveningNode.instanceIds AS currentInstanceId " +
        "MATCH p=(:InstanceStartActivity {instanceId: currentInstanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: currentInstanceId}) " +
        "UNWIND relationships(p) AS rel " +
        "WITH currentInstanceId, startNode(rel) AS start, endNode(rel) AS end " +
        "RETURN currentInstanceId, ID(start) AS startNodeId, start.type AS startNodeType, collect(distinct ID(end)) AS endNodeIds, end.type AS endNodeType, count(distinct end) AS cardinality ")
    List<InstanceBasedEndCardinalityQueryResult> findInstanceBasedEndCardinalities(@Param("eventType") String eventType, @Param("origId") String origId);


//    @Query("MATCH (conveningNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
//            "UNWIND conveningNode.instanceIds AS currentInstanceId " +
//            "MATCH p=(:InstanceStartActivity {instanceId: currentInstanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: currentInstanceId}) " +
//            "UNWIND relationships(p) AS rel " +
//            "WITH currentInstanceId, startNode(rel) AS start, endNode(rel) AS end " +
//            "WITH currentInstanceId, end, end.type AS endNodeType, start.type AS startNodeType, collect(distinct start) AS collectedStart " +
//            "RETURN currentInstanceId, ID(end) AS endNodeId, endNodeType, [x IN collectedStart | ID(x)] AS startNodeIds, startNodeType, size(collectedStart) AS cardinality")
    @Query("MATCH (conveningNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
            "UNWIND conveningNode.instanceIds AS currentInstanceId " +
            "MATCH p=(:InstanceStartActivity {instanceId: currentInstanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: currentInstanceId}) " +
            "UNWIND relationships(p) AS rel " +
            "WITH currentInstanceId, startNode(rel) AS start, endNode(rel) AS end " +
            "RETURN currentInstanceId, ID(end) AS endNodeId, end.type AS endNodeType, collect(distinct ID(start)) AS startNodeIds, start.type AS startNodeType, count(distinct start) AS cardinality ")
    List<InstanceBasedStartCardinalityQueryResult> findInstanceBasedStartCardinalities(@Param("eventType") String eventType, @Param("origId") String origId);

    @Query("MATCH (conveningNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
            "UNWIND conveningNode.instanceIds AS currentInstanceId " +
            "MATCH p=(:InstanceStartActivity {instanceId: currentInstanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: currentInstanceId}) " +
            "UNWIND relationships(p) AS rel " +
            "WITH currentInstanceId, startNode(rel) AS start, endNode(rel) AS end " +
            "WITH currentInstanceId, start, end.type AS endNodeType, count(distinct end) AS cardinality " +
            "RETURN start.type AS startNodeType, endNodeType, max(cardinality) AS maxCardinality")
    List<CardinalityQueryResult> findAllSelectedInstancesEndCardinalities(@Param("eventType") String eventType, @Param("origId") String origId);

    @Query("MATCH (conveningNode:InstanceIntermediateActivity {type: $eventType, `origIds.SpedNr`: $origId}) " +
            "UNWIND conveningNode.instanceIds AS currentInstanceId " +
            "MATCH p=(:InstanceStartActivity {instanceId: currentInstanceId})-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: currentInstanceId}) " +
            "UNWIND relationships(p) AS rel " +
            "WITH currentInstanceId, startNode(rel) AS start, endNode(rel) AS end " +
            "WITH currentInstanceId, end, start.type AS startNodeType, count(distinct start) AS cardinality " +
            "RETURN end.type AS endNodeType, startNodeType, max(cardinality) AS maxCardinality")
    List<CardinalityQueryResult> findAllSelectedInstancesStartCardinalities(@Param("eventType") String eventType, @Param("origId") String origId);

    @Query("MATCH p=(startActivity:InstanceStartActivity)-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: startActivity.instanceId}) " +
            "UNWIND relationships(p) AS rel " +
            "WITH startActivity.instanceId AS currentInstanceId, startNode(rel) AS start, endNode(rel) AS end " +
            "WITH currentInstanceId, start, end.type AS endNodeType, count(distinct end) AS cardinality " +
            "RETURN start.type AS startNodeType, endNodeType, max(cardinality) AS maxCardinality")
    List<CardinalityQueryResult> findAllEndCardinalities();

    @Query("MATCH p=(startActivity:InstanceStartActivity)-[:INSTANCE_FOLLOW_UP*..10]->(:InstanceEndActivity {instanceId: startActivity.instanceId}) " +
            "UNWIND relationships(p) AS rel " +
            "WITH startActivity.instanceId AS currentInstanceId, startNode(rel) AS start, endNode(rel) AS end " +
            "WITH currentInstanceId, end, start.type AS startNodeType, count(distinct start) AS cardinality " +
            "RETURN end.type AS endNodeType, startNodeType, max(cardinality) AS maxCardinality")
    List<CardinalityQueryResult> findAllStartCardinalities();

}

