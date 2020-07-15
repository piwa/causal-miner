package at.ac.wuwien.causalminer.neo4jdb.services;

import at.ac.wuwien.causalminer.neo4jdb.configuration.LogExecutionTime;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.*;
import at.ac.wuwien.causalminer.neo4jdb.repositories.AggregatedProcessInstanceRepository;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Transactional("graphTransactionManager")
public class AggregateProcessInstanceServices {

    @Autowired
    private AggregatedProcessInstanceRepository aggregatedProcessInstanceRepository;

    @LogExecutionTime
    public List<ModelActivity> aggregateTotalByEvent(String eventType, String origId) {
        AggregateNeighborsQueryResult queryResult = aggregatedProcessInstanceRepository.aggregateTotalBySpedEvent(eventType, origId);

        // Add relationship cardinalities
        Map<String, List<ModelRelationship>> sourceNodeTypeModelRelationship = new HashMap<>();
        Map<String, List<ModelRelationship>> targetNodeTypeModelRelationship = new HashMap<>();

        queryResult.getModelRelationshipList().forEach(relationship -> {
            String startNodeType = relationship.getStartEvent().getType();
            String endNodeType = relationship.getEndEvent().getType();
            sourceNodeTypeModelRelationship.computeIfAbsent(startNodeType, k -> new ArrayList<>()).add(relationship);
            targetNodeTypeModelRelationship.computeIfAbsent(endNodeType, k -> new ArrayList<>()).add(relationship);
        });

        List<CardinalityQueryResult> instanceBasedStartCardinalityQueryResults = findAllSelectedInstancesStartCardinalities(eventType, origId);
        instanceBasedStartCardinalityQueryResults.forEach(result -> {

            List<ModelRelationship> availableRelations = targetNodeTypeModelRelationship.get(result.getEndNodeType());

            for (ModelRelationship relationship : availableRelations) {
                if (result.getStartNodeType().equals(relationship.getStartEvent().getType())) {
                    if (result.getMaxCardinality() > 1) {
                        relationship.setStartCardinality(Integer.MAX_VALUE);  // TODO this is here to say something like N:1 but it is not really nice to do it that way
                    } else {
                        relationship.setStartCardinality(result.getMaxCardinality());
                    }
                }
            }
        });

        List<CardinalityQueryResult> instanceBasedEndCardinalityQueryResults = findAllSelectedInstancesEndCardinalities(eventType, origId);
        instanceBasedEndCardinalityQueryResults.forEach(result -> {

            List<ModelRelationship> availableRelations = sourceNodeTypeModelRelationship.get(result.getStartNodeType());

            for (ModelRelationship relationship : availableRelations) {
                if (result.getEndNodeType().equals(relationship.getEndEvent().getType())) {
                    if (result.getMaxCardinality() > 1) {
                        relationship.setEndCardinality(Integer.MAX_VALUE);  // TODO this is here to say something like N:1 but it is not really nice to do it that way
                    } else {
                        relationship.setEndCardinality(result.getMaxCardinality());
                    }
                }
            }
        });

        // add asymmetric conflict relationships
        List<NullModelActivitiesQueryResult> nullModelActivitiesQueryResults = aggregatedProcessInstanceRepository.getNullModelActivities(eventType, origId);
        Map<UUID, List<String>> uuidListMap = new HashMap<>();
        nullModelActivitiesQueryResults.forEach(element -> uuidListMap.put(element.getCurrentInstanceId(), element.getNullActivityTypes()));

        for (Map.Entry<UUID, List<String>> uuidListEntry : uuidListMap.entrySet()) {

            String uuid = uuidListEntry.getKey().toString();
            List<String> types = uuidListEntry.getValue();

            queryResult.getModelRelationshipList().stream() // TODO at the moment we only make a relationship asymmetric if the end is null
                    .filter(rel -> rel.getStartEvent().getOrigNodeInstanceIds().contains(uuid) || rel.getEndEvent().getOrigNodeInstanceIds().contains(uuid))
//                    .filter(rel -> types.contains(rel.getStartEvent().getType()) || types.contains(rel.getEndEvent().getType()))
                    .filter(rel -> types.contains(rel.getEndEvent().getType()))
                    .forEach(rel -> rel.setAsymmetricConflictRelationship(true));
        }


//        for (ModelRelationship modelRelationship : queryResult.getModelRelationshipList()) {
//
//            Set<String> instanceIds = new HashSet<>(modelRelationship.getStartEvent().getOrigNodeInstanceIds());
//            instanceIds.addAll(modelRelationship.getEndEvent().getOrigNodeInstanceIds());
//
//            instanceIds.forEach( id -> {
//
//                if(uuidListMap.containsKey(UUID.fromString(id))) {
//                    List<String> types = uuidListMap.get(UUID.fromString(id));
//
//                    if(types.contains(modelRelationship.getStartEvent().getType()) || types.contains(modelRelationship.getEndEvent().getType())){
//                        modelRelationship.setAsymmetricConflictRelationship(true);
//                    }
//                }
//            });
//        }

        return queryResult.getModelActivityList();
    }

    @LogExecutionTime
    public List<InstanceActivity> findByEvent(String eventType, String origId) {
        return aggregatedProcessInstanceRepository.findByEvent(eventType, origId);
    }

    @LogExecutionTime
    public List<ModelActivity> findByEventAndAggregateNeighbors(String eventType, String origId) {
        AggregateNeighborsQueryResult queryResult = aggregatedProcessInstanceRepository.findByEventAndAggregateNeighbors(eventType, origId);
        Map<Integer, List<ModelActivity>> idModelActivityMap = new HashMap<>();
        // TODO use something else than the .hashCode()
        queryResult.getModelActivityList().forEach(instanceActivity ->
                idModelActivityMap.computeIfAbsent(instanceActivity.getOrigNodeIds().hashCode(), k -> new ArrayList<>()).add(instanceActivity)
        );


        // Add relationship cardinalities
        Map<Long, List<ModelRelationship>> sourceNodeIdModelRelationship = new HashMap<>();
        Map<Long, List<ModelRelationship>> targetNodeIdModelRelationship = new HashMap<>();

        queryResult.getModelRelationshipList().forEach(relationship -> {
            relationship.getStartEvent().getOrigNodeIds().forEach(id ->
                    sourceNodeIdModelRelationship.computeIfAbsent(id, k -> new ArrayList<>()).add(relationship)
            );

            relationship.getEndEvent().getOrigNodeIds().forEach(id ->
                    targetNodeIdModelRelationship.computeIfAbsent(id, k -> new ArrayList<>()).add(relationship)
            );
        });

        List<InstanceBasedStartCardinalityQueryResult> instanceBasedStartCardinalityQueryResults = findInstanceBasedStartCardinalities(eventType, origId);
        instanceBasedStartCardinalityQueryResults.forEach(result -> {

            List<ModelRelationship> availableRelations = targetNodeIdModelRelationship.get(result.getEndNodeId());

            for (ModelRelationship relationship : availableRelations) {
                if (result.getStartNodeIds().stream().anyMatch(startNodeId -> relationship.getStartEvent().getOrigNodeIds().contains(startNodeId))) {
                    relationship.setStartCardinality(result.getCardinality());
                }
            }

        });

        List<InstanceBasedEndCardinalityQueryResult> instanceBasedEndCardinalityQueryResults = findInstanceBasedEndCardinalities(eventType, origId);
        instanceBasedEndCardinalityQueryResults.forEach(result -> {

            List<ModelRelationship> availableRelations = sourceNodeIdModelRelationship.get(result.getStartNodeId());

            for (ModelRelationship relationship : availableRelations) {
                if (result.getEndNodeIds().stream().anyMatch(endNodeId -> relationship.getEndEvent().getOrigNodeIds().contains(endNodeId))) {
                    relationship.setEndCardinality(result.getCardinality());
                }
            }

        });


        // add asymmetric conflict relationships
        List<NullModelActivitiesQueryResult> nullModelActivitiesQueryResults = aggregatedProcessInstanceRepository.getNullModelActivities(eventType, origId);
        Map<UUID, List<String>> uuidListMap = new HashMap<>();
        nullModelActivitiesQueryResults.forEach(element -> uuidListMap.put(element.getCurrentInstanceId(), element.getNullActivityTypes()));

        uuidListMap.forEach((key, types) -> {
            String uuid = key.toString();
            queryResult.getModelRelationshipList().stream() // TODO at the moment we only make a relationship asymmetric if the end is null
                    .filter(rel -> rel.getStartEvent().getOrigNodeInstanceIds().contains(uuid) || rel.getEndEvent().getOrigNodeInstanceIds().contains(uuid))
//                    .filter(rel -> types.contains(rel.getStartEvent().getType()) || types.contains(rel.getEndEvent().getType()))
                    .filter(rel -> types.contains(rel.getEndEvent().getType()))
                    .forEach(rel -> rel.setAsymmetricConflictRelationship(true));
        });


        // Combine the resulting graphs by common nodes
        List<ModelActivity> modelActivitiesToDelete = new ArrayList<>();
        idModelActivityMap.entrySet().stream().filter(entry -> entry.getValue().size() > 1).forEach(entry -> {

            Iterator<ModelActivity> similarActivities = entry.getValue().iterator();
            ModelActivity firstActivity = similarActivities.next();

            Map<RelationshipSourceTarget, Set<ModelRelationship>> changedRelations = new HashMap<>();

            queryResult.getModelRelationshipList().stream()
                    .filter(rel -> rel.getStartEvent().equals(firstActivity) || rel.getEndEvent().equals(firstActivity))
                    .forEach(rel -> {
                        RelationshipSourceTarget relationshipSourceTarget = null;
                        if(rel.getStartEvent().equals(firstActivity)) {
                            relationshipSourceTarget = new RelationshipSourceTarget(rel.getStartEvent().getType(), rel.getEndEvent().getType(), firstActivity, false);
                        }
                        else {
                            relationshipSourceTarget = new RelationshipSourceTarget(rel.getStartEvent().getType(), rel.getEndEvent().getType(), firstActivity, true);
                        }
                        changedRelations.computeIfAbsent(relationshipSourceTarget, k -> new HashSet<>()).add(rel);
                    });

            similarActivities.forEachRemaining(activity -> {
                queryResult.getModelRelationshipList().stream()
                        .filter(rel -> rel.getStartEvent().equals(activity))
                        .forEach(rel -> {
                            rel.setStartEvent(firstActivity);
                            firstActivity.getFollowUpRelationships().add(rel);
                            RelationshipSourceTarget relationshipSourceTarget = new RelationshipSourceTarget(rel.getStartEvent().getType(), rel.getEndEvent().getType(), firstActivity, false);
                            changedRelations.computeIfAbsent(relationshipSourceTarget, k -> new HashSet<>()).add(rel);
                        });

                queryResult.getModelRelationshipList().stream()
                        .filter(rel -> rel.getEndEvent().equals(activity))
                        .forEach(rel -> {
                            rel.setEndEvent(firstActivity);
                            RelationshipSourceTarget relationshipSourceTarget = new RelationshipSourceTarget(rel.getStartEvent().getType(), rel.getEndEvent().getType(), firstActivity, true);
                            changedRelations.computeIfAbsent(relationshipSourceTarget, k -> new HashSet<>()).add(rel);
                        });

                modelActivitiesToDelete.add(activity);
            });

            // TODO The next lines would increase the cardinality when we have a combined node
//            changedRelations.forEach((relationshipSourceTarget, relationships) -> {
//                if (relationshipSourceTarget.startChardinalty) {
//                    relationships.forEach(rel -> rel.setStartCardinality(rel.getStartCardinality() + relationships.size() - 1));
//                } else {
//                    relationships.forEach(rel -> rel.setEndCardinality(rel.getEndCardinality() + relationships.size() - 1));
//                }
//            });
        });



        queryResult.getModelActivityList().removeAll(modelActivitiesToDelete);

        return queryResult.getModelActivityList();
    }

    @LogExecutionTime
    public List<EventTypeResult> getEventTypes(String eventType) {
        return aggregatedProcessInstanceRepository.findAmountOfConnectedInstances(eventType);
    }

    @LogExecutionTime
    public List<EventTypeResult> getEventTypes(String eventType, String origId) {
        return aggregatedProcessInstanceRepository.findAmountOfConnectedInstances(eventType, origId);
    }

    @LogExecutionTime
    public List<InstanceBasedEndCardinalityQueryResult> findInstanceBasedEndCardinalities(String eventType, String origId) {
        return aggregatedProcessInstanceRepository.findInstanceBasedEndCardinalities(eventType, origId);
    }

    @LogExecutionTime
    public List<InstanceBasedStartCardinalityQueryResult> findInstanceBasedStartCardinalities(String eventType, String origId) {
        return aggregatedProcessInstanceRepository.findInstanceBasedStartCardinalities(eventType, origId);
    }

    @LogExecutionTime
    public List<CardinalityQueryResult> findAllEndCardinalities() {
        return aggregatedProcessInstanceRepository.findAllEndCardinalities();
    }

    @LogExecutionTime
    public List<CardinalityQueryResult> findAllStartCardinalities() {
        return aggregatedProcessInstanceRepository.findAllStartCardinalities();
    }

    @LogExecutionTime
    public List<CardinalityQueryResult> findAllSelectedInstancesStartCardinalities(String eventType, String origId) {
        return aggregatedProcessInstanceRepository.findAllSelectedInstancesStartCardinalities(eventType, origId);
    }

    @LogExecutionTime
    public List<CardinalityQueryResult> findAllSelectedInstancesEndCardinalities(String eventType, String origId) {
        return aggregatedProcessInstanceRepository.findAllSelectedInstancesEndCardinalities(eventType, origId);
    }

    protected void addCardinalitiesToRelationships(AggregateNeighborsQueryResult queryResult, List<CardinalityQueryResult> instanceBasedStartCardinalityQueryResults, List<CardinalityQueryResult> instanceBasedEndCardinalityQueryResults) {
        Map<String, List<ModelRelationship>> sourceNodeTypeModelRelationship = new HashMap<>();
        Map<String, List<ModelRelationship>> targetNodeTypeModelRelationship = new HashMap<>();

        queryResult.getModelRelationshipList().forEach(relationship -> {
            String startNodeType = relationship.getStartEvent().getType();
            String endNodeType = relationship.getEndEvent().getType();
            sourceNodeTypeModelRelationship.computeIfAbsent(startNodeType, k -> new ArrayList<>()).add(relationship);
            targetNodeTypeModelRelationship.computeIfAbsent(endNodeType, k -> new ArrayList<>()).add(relationship);
        });

        instanceBasedStartCardinalityQueryResults.stream().filter(result -> targetNodeTypeModelRelationship.containsKey(result.getEndNodeType()))
                .forEach(result -> {

            for (ModelRelationship relationship : targetNodeTypeModelRelationship.get(result.getEndNodeType())) {
                if (result.getStartNodeType().equals(relationship.getStartEvent().getType())) {
                    if (result.getMaxCardinality() > 1) {
                        relationship.setStartCardinality(Integer.MAX_VALUE);  // TODO this is here to say something like N:1 but it is not really nice to do it that way
                    } else {
                        relationship.setStartCardinality(result.getMaxCardinality());
                    }
                }
            }
        });

        instanceBasedEndCardinalityQueryResults.stream().filter(result -> sourceNodeTypeModelRelationship.containsKey(result.getStartNodeType()))
                .forEach(result -> {

            for (ModelRelationship relationship : sourceNodeTypeModelRelationship.get(result.getStartNodeType())) {
                if (result.getEndNodeType().equals(relationship.getEndEvent().getType())) {
                    if (result.getMaxCardinality() > 1) {
                        relationship.setEndCardinality(Integer.MAX_VALUE);  // TODO this is here to say something like N:1 but it is not really nice to do it that way
                    } else {
                        relationship.setEndCardinality(result.getMaxCardinality());
                    }
                }
            }
        });
    }

    @Data
    private class RelationshipSourceTarget {

        private String sourceNodeType;
        private String targetNodeType;
        private Boolean startChardinalty;
        private ModelActivity commonNode;

        public RelationshipSourceTarget(String sourceNodeType, String targetNodeType, ModelActivity commonNode, Boolean startChardinalty) {
            this.sourceNodeType = sourceNodeType;
            this.targetNodeType = targetNodeType;
            this.startChardinalty = startChardinalty;
            this.commonNode = commonNode;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RelationshipSourceTarget that = (RelationshipSourceTarget) o;
            return Objects.equals(getSourceNodeType(), that.getSourceNodeType()) &&
                    Objects.equals(getTargetNodeType(), that.getTargetNodeType()) &&
                    Objects.equals(getStartChardinalty(), that.getStartChardinalty()) &&
                    Objects.equals(getCommonNode(), that.getCommonNode());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getSourceNodeType(), getTargetNodeType(), getStartChardinalty(), getCommonNode());
        }
    }
}
