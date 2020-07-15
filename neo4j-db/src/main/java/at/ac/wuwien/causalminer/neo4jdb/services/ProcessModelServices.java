package at.ac.wuwien.causalminer.neo4jdb.services;

import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelIntermediateActivity;
import at.ac.wuwien.causalminer.neo4jdb.configuration.LogExecutionTime;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.AggregateNeighborsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.CardinalityQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.CombinableIndividualModelsResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.ModelCheckerResult;
import at.ac.wuwien.causalminer.neo4jdb.repositories.ModelStartActivityRepository;
import at.ac.wuwien.causalminer.neo4jdb.repositories.ProcessModelRepository;
import org.joda.time.DateTime;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional("graphTransactionManager")
public class ProcessModelServices {

    @Autowired
    private ProcessModelRepository processModelRepository;
    @Autowired
    private ModelStartActivityRepository modelStartActivityRepository;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private AggregateProcessInstanceServices aggregateProcessInstanceServices;

    @LogExecutionTime
    public ModelIntermediateActivity save(ModelIntermediateActivity groupedNode) {
        return processModelRepository.save(groupedNode);
    }

    @LogExecutionTime
    public Iterable<ModelActivity> saveAll(List<ModelActivity> groupedNodes) {
        return processModelRepository.saveAll(groupedNodes);
    }

    @LogExecutionTime
    public void deleteAll() {
        processModelRepository.deleteAll();
    }


    /* ----------- Global Process Models ----------- */
    @LogExecutionTime
    public List<ModelActivity> createGlobalProcessModel() {

        AggregateNeighborsQueryResult queryResult = processModelRepository.createGlobalProcessModel();

        // Add relationship cardinalities
        List<CardinalityQueryResult> instanceBasedStartCardinalityQueryResults = aggregateProcessInstanceServices.findAllStartCardinalities();
        List<CardinalityQueryResult> instanceBasedEndCardinalityQueryResults = aggregateProcessInstanceServices.findAllEndCardinalities();

        aggregateProcessInstanceServices.addCardinalitiesToRelationships(queryResult, instanceBasedStartCardinalityQueryResults, instanceBasedEndCardinalityQueryResults);

        return queryResult.getModelActivityList();
    }

    @LogExecutionTime
    public List<ModelActivity> createGlobalProcessModelWithAsymmetricConflicts() {

        AggregateNeighborsQueryResult queryResult = processModelRepository.createGlobalProcessModel();

        // Add relationship cardinalities
        List<CardinalityQueryResult> instanceBasedStartCardinalityQueryResults = aggregateProcessInstanceServices.findAllStartCardinalities();
        List<CardinalityQueryResult> instanceBasedEndCardinalityQueryResults = aggregateProcessInstanceServices.findAllEndCardinalities();

        aggregateProcessInstanceServices.addCardinalitiesToRelationships(queryResult, instanceBasedStartCardinalityQueryResults, instanceBasedEndCardinalityQueryResults);

        AggregateNeighborsQueryResult nullActivityQueryResult = processModelRepository.createGlobalNullActivitiesProcessModel();

        Set<ModelActivity> relEndNodes = nullActivityQueryResult.getModelRelationshipList().stream().map(ModelRelationship::getEndEvent).collect(Collectors.toSet());

        nullActivityQueryResult.getModelActivityList().stream()
                .filter(act -> !relEndNodes.contains(act))
                .forEach(modelNullActivity -> {
                    queryResult.getModelRelationshipList().stream() // TODO at the moment we only make a relationship asymmetric if the end is null
//                    .filter(rel -> rel.getStartEvent().getType().equals(modelNullActivity.getType()) || rel.getEndEvent().getType().equals(modelNullActivity.getType()))
                            .filter(rel -> rel.getEndEvent().getType().equals(modelNullActivity.getType()))
                            .forEach(rel -> rel.setAsymmetricConflictRelationship(true));
                });

        return queryResult.getModelActivityList();
    }

    @LogExecutionTime
    public List<ModelActivity> findGlobalProcessModel() {
        return processModelRepository.findGlobalProcessModel();
    }


    /* ----------- Individual Process Models ----------- */
    @LogExecutionTime
    public List<ModelActivity> createIndividualProcessModelForInstanceId(String instanceId) {
        return processModelRepository.createIndividualProcessModelForInstanceId(instanceId);
    }

    @LogExecutionTime
    public List<ModelActivity> findIndividualProcessModelByModelId(String modelId) {
        return processModelRepository.findIndividualProcessModelByModelId(modelId);
    }

    @LogExecutionTime
    public List<ModelStartActivity> findAllIndividualProcessModels() {
        return modelStartActivityRepository.findAllIndividualProcessModels();
    }

    /* ----------- Aggregated Process Models ----------- */
    @LogExecutionTime
    public List<ModelActivity> createAggregatedProcessModel(String eventType, String origId) {
        return processModelRepository.createAggregatedProcessModel(eventType, origId);
    }

    /* ----------- Combined Process Models ----------- */
    @LogExecutionTime
    public List<ModelActivity> createProcessModelOfTimeFrame(DateTime startTime, DateTime endTime) {
        return processModelRepository.createProcessModelOfTimeFrame(startTime.toString(), endTime.toString());
    }

    @LogExecutionTime
    public List<ModelActivity> createCombinedProcessModelForInstanceIds(Collection<String> instanceIds) {
        return processModelRepository.createCombinedProcessModelForInstanceIds(instanceIds);
    }

    @LogExecutionTime
    public List<ModelStartActivity> findAllCombinedProcessModels() {
        return modelStartActivityRepository.findAllCombinedProcessModels();
    }

    @LogExecutionTime
    public List<ModelCheckerResult> findPartialMatchingProcessModels(List<List<String>> nodeTypeList) {
        PartialModelCheckerQuery query = new PartialModelCheckerQuery();
        String queryString = query.addMatchClauses(nodeTypeList).addReturnAllMatchingModelIds().build();
        return queryDbForModelCheckerResults(queryString);
    }

    @LogExecutionTime
    public List<ModelCheckerResult> findPartialNotMatchingProcessModels(List<List<String>> nodeTypeList) {
        PartialModelCheckerQuery query = new PartialModelCheckerQuery();
        String queryString = query.addMatchClauses(nodeTypeList).addCollectMatchingModelIds().addReturnAllNotMatchingModelStartActivities().build();
        return queryDbForModelCheckerResults(queryString);
    }

    @LogExecutionTime
    public List<ModelCheckerResult> findPartialNotMatchingProcessModelsUserFunction(List<List<String>> nodeTypeList) {
        return processModelRepository.findPartialNotMatchingProcessModelsUserFunction(nodeTypeList);
    }

    @LogExecutionTime
    public List<ModelCheckerResult> findCompleteMatchingProcessModels(List<List<String>> nodeTypeList) {
        CompleteModelCheckerQuery query = new CompleteModelCheckerQuery();
        String queryString = query.addMatchClauses(nodeTypeList).addCollectMatchingModelIds().addReturnAllMatchingModelIds().build();
        return queryDbForModelCheckerResults(queryString);
    }

    @LogExecutionTime
    public List<ModelCheckerResult> findCompleteNotMatchingProcessModels(List<List<String>> nodeTypeList) {
        CompleteModelCheckerQuery query = new CompleteModelCheckerQuery();
        String queryString = query.addMatchClauses(nodeTypeList).addCollectMatchingModelIds().addReturnAllNotMatchingModelStartActivities().build();
        return queryDbForModelCheckerResults(queryString);
    }

    @LogExecutionTime
    public List<CombinableIndividualModelsResult> findAllCasesWithSameProcessModel() {
        return processModelRepository.findAllCombinableIndividualModels();
    }

    @LogExecutionTime
    public List<ModelActivity> findByModelId(String modelId) {
        return processModelRepository.findByModelId(modelId);
    }


    private List<ModelCheckerResult> queryDbForModelCheckerResults(String queryString) {
        Iterable<ModelStartActivity> result = sessionFactory.openSession().query(ModelStartActivity.class, queryString, new HashMap<>());

        List<ModelCheckerResult> modelCheckerResults = new ArrayList<>();
        result.forEach(modelStartActivity -> {
            List<ModelCheckerResult> instanceIds = modelStartActivity.getInstanceOfRelationships().stream()
                    .map(rel -> new ModelCheckerResult(((InstanceStartActivity) rel.getStartEvent()).getInstanceId()))
                    .collect(Collectors.toList());
            modelCheckerResults.addAll(instanceIds);
        });

        return modelCheckerResults;
    }
}
