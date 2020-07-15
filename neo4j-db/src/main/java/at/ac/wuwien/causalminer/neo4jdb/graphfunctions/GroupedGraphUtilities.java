package at.ac.wuwien.causalminer.neo4jdb.graphfunctions;

import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.*;
import at.ac.wuwien.causalminer.neo4jdb.services.ProcessModelServices;
import at.ac.wuwien.causalminer.neo4jdb.services.StartActivityServices;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.OfRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GroupedGraphUtilities {

    @Autowired
    private StartActivityServices startActivityServices;
    @Autowired
    private ProcessModelServices processModelServices;

    public List<ModelActivity> findProcessModelOfTimeFrame(DateTime startTime, DateTime endTime) {
        List<ModelActivity> groupedNodes = processModelServices.createProcessModelOfTimeFrame(startTime, endTime);
        groupedNodes.forEach(this::calculateRelationshipProbabilities);
        return groupedNodes;
    }

    public void calculateRelationshipProbabilities(ModelActivity startNode) {
        int totalRelationships = startNode.getFollowUpRelationships().stream().mapToInt(ModelRelationship::getCount).sum();

        startNode.getFollowUpRelationships().forEach(relationship -> {
            double probability = Math.round((double) relationship.getCount() * 100 / totalRelationships);
            relationship.setProbability(probability);
        });
    }

    public List<ModelActivity> createAndSaveIndividualProcessModel(InstanceStartActivity startActivities) {
        String instanceId = startActivities.getInstanceId();
        List<ModelActivity> groupedNodes = processModelServices.createIndividualProcessModelForInstanceId(instanceId);
        final UUID modelId = UUID.randomUUID();
        groupedNodes.forEach(groupedNode -> {
            calculateRelationshipProbabilities(groupedNode);
            if (groupedNode instanceof ModelStartActivity) {
                ((ModelStartActivity) groupedNode).setModelId(modelId.toString());
                OfRelationship ofRelationship = new OfRelationship(startActivities, groupedNode);
                startActivities.getInstanceOfRelationships().add(ofRelationship);
            } else if (groupedNode instanceof ModelEndActivity) {
                ((ModelEndActivity) groupedNode).setModelId(modelId.toString());
            }
            if (groupedNode.getType().startsWith("NULL_")) {
                groupedNode.getLabels().add("NULL");
            }
        });

//        processModelServices.saveAll(groupedNodes);
        startActivityServices.save(startActivities);


        return groupedNodes;
    }

    public List<ModelActivity> createAndSaveCombinedProcessModel(Collection<InstanceStartActivity> startActivities) {
        Set<String> instanceIds = startActivities.stream().map(InstanceStartActivity::getInstanceId).collect(Collectors.toSet());
        List<ModelActivity> groupedNodes = processModelServices.createCombinedProcessModelForInstanceIds(instanceIds);
        final UUID modelId = UUID.randomUUID();
        groupedNodes.forEach(groupedNode -> {
            calculateRelationshipProbabilities(groupedNode);
            if (groupedNode instanceof ModelStartActivity) {
                ((ModelStartActivity)groupedNode).setModelId(modelId.toString());
                startActivities.forEach(startActivity -> {
                    OfRelationship ofRelationship = new OfRelationship(startActivity, groupedNode);
                    startActivity.getInstanceOfRelationships().add(ofRelationship);
                });
            } else if (groupedNode instanceof ModelEndActivity) {
                ((ModelEndActivity)groupedNode).setModelId(modelId.toString());
            }
            if (groupedNode.getType().startsWith("NULL_")) {
                groupedNode.getLabels().add("NULL");
            }
        });
//        processModelServices.saveAll(groupedNodes);
        startActivityServices.saveAll(startActivities);
        return groupedNodes;
    }

    public List<ModelActivity> createAndSaveGlobalGroupedGraph() {
        List<ModelActivity> groupedNodes = processModelServices.createGlobalProcessModelWithAsymmetricConflicts();
        final UUID modelId = UUID.randomUUID();
        groupedNodes.forEach(groupedNode -> {
            calculateRelationshipProbabilities(groupedNode);
            if (groupedNode instanceof ModelStartActivity || groupedNode instanceof ModelEndActivity) {
                ((AbstractModelStartEndActivity)groupedNode).setModelId(modelId.toString());
            }
            if (groupedNode.getType().startsWith("NULL_")) {
                groupedNode.getLabels().add("NULL");
            }
        });
        processModelServices.saveAll(groupedNodes);
        return groupedNodes;
    }
}
