package at.ac.wuwien.causalminer.neo4jdb.repositories;

import at.ac.wuwien.causalminer.neo4jdb.domain.IHomogeneousRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.input.AsymmetricConflictInput;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.EndCardinalityViolationsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.StartCardinalityViolationsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.input.CardinalityInputVariables;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TemplateViolationRepository extends Neo4jRepository<InstanceActivity, Long> {

    @Query("MATCH p=(a:ProcessInstance)-[r:INSTANCE_FOLLOW_UP]->(b:ProcessInstance) WHERE r.duration < 0 RETURN r, a, b")
    List<InstanceRelationship> findAllTemporalRelationshipViolations();

    //   @Query("MATCH p=(startNode:InstanceStartActivity)-[:INSTANCE_FOLLOW_UP*..10]->(endNode {instanceId: startNode.instanceId}) " +
//           "UNWIND nodes(p) AS nodes " +
//           "WITH startNode, endNode, collect(distinct nodes) AS tempNode " +
//           "UNWIND tempNode AS n " +
//           "MATCH (n)-[:INSTANCE_FOLLOW_UP]->(a) " +
//           "WHERE startNode.instanceId IN a.instanceIds OR endNode = a " +
//           "RETURN startNode.instanceId AS instanceId, ID(n) AS currentNodeId, n.type AS currentNodeType, a.type AS relatedNodeType, count(ID(a)) AS cardinality")
    @Query("WITH $inputVariables AS inputVariables " +
            "UNWIND inputVariables AS inputVariable " +
            "MATCH (sourceNode:InstanceIntermediateActivity {type: inputVariable.sourceNodeType})-[:INSTANCE_FOLLOW_UP]->(targetNode:InstanceIntermediateActivity {type: inputVariable.targetNodeType}) " +
            "UNWIND sourceNode.instanceIds AS instanceId " +
            "WITH instanceId, ID(sourceNode) AS sourceNodeId, sourceNode.type AS sourceNodeType, targetNode.type AS targetNodeType, targetNode.instanceIds AS targetInstanceIds, inputVariable AS inputVariable, collect(ID(targetNode)) as targetNodeIds, count(targetNode) AS cardinality " +
            "WHERE instanceId IN targetInstanceIds AND (inputVariable.nCardinality = false AND cardinality <> inputVariable.requiredCardinality) " +
            "RETURN instanceId, sourceNodeId, targetNodeIds, sourceNodeType, targetNodeType, cardinality")
    List<EndCardinalityViolationsQueryResult> findAllEndCardinalityViolations(@Param("inputVariables") Collection<CardinalityInputVariables> inputVariables);

    //   @Query("MATCH p=(startNode:InstanceStartActivity)-[:INSTANCE_FOLLOW_UP*..10]->(endNode {instanceId: startNode.instanceId}) " +
//           "UNWIND nodes(p) AS nodes " +
//           "WITH startNode, collect(distinct nodes) AS tempNode " +
//           "UNWIND tempNode AS n " +
//           "MATCH (a)-[:INSTANCE_FOLLOW_UP]->(n) " +
//           "WHERE startNode.instanceId IN a.instanceIds OR startNode = a " +
//           "RETURN startNode.instanceId AS instanceId, ID(n) AS currentNodeId, n.type AS currentNodeType, a.type AS relatedNodeType, count(ID(a)) AS cardinality")
    @Query("WITH $inputVariables AS inputVariables " +
            "UNWIND inputVariables AS inputVariable " +
            "MATCH (sourceNode:InstanceIntermediateActivity {type: inputVariable.sourceNodeType})-[:INSTANCE_FOLLOW_UP]->(targetNode:InstanceIntermediateActivity {type: inputVariable.targetNodeType}) " +
            "UNWIND targetNode.instanceIds AS instanceId " +
            "WITH instanceId, ID(targetNode) AS targetNodeId, targetNode.type AS targetNodeType, sourceNode.type AS sourceNodeType, sourceNode.instanceIds AS sourceInstanceIds, inputVariable AS inputVariable, collect(ID(sourceNode)) as sourceNodeIds, count(sourceNode) AS cardinality " +
            "WHERE instanceId IN sourceInstanceIds AND (inputVariable.nCardinality = false AND cardinality <> inputVariable.requiredCardinality) " +
            "RETURN instanceId, targetNodeId, sourceNodeIds, sourceNodeType, targetNodeType, cardinality")
    List<StartCardinalityViolationsQueryResult> findAllStartCardinalityViolations(@Param("inputVariables") Collection<CardinalityInputVariables> inputVariables);


    @Query("MATCH (a:InstanceNullActivity)-[r:NULL_INSTANCE_FOLLOW_UP]->(b:InstanceActivity) " +
            "WHERE (NOT 'InstanceStartActivity' IN labels(a)) AND (NOT 'InstanceEndActivity' IN labels(b)) AND (NOT {sourceNodeType: a.type, targetNodeType: b.type} IN $asymmetricConflicts) " +
            "RETURN r,a,b")
    List<IHomogeneousRelationship<InstanceActivity>> findAllAsymmetricConflictViolations(@Param("asymmetricConflicts") Collection<AsymmetricConflictInput> asymmetricConflicts);

}
