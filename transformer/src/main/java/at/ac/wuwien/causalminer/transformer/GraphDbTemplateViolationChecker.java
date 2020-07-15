package at.ac.wuwien.causalminer.transformer;

import at.ac.wuwien.causalminer.transformer.model.template.CardinalRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.IHomogeneousRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceNullActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.input.AsymmetricConflictInput;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.input.CardinalityInputVariables;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceIntermediateActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.EndCardinalityViolationsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.StartCardinalityViolationsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.services.TemplateViolationServices;
import at.ac.wuwien.causalminer.erp2graphdb.model.TransferredProcessInstance;
import at.ac.wuwien.causalminer.erp2graphdb.model.ViolatingNode;
import at.ac.wuwien.causalminer.erp2graphdb.model.Violation;
import at.ac.wuwien.causalminer.erp2graphdb.model.ViolationType;
import at.ac.wuwien.causalminer.erp2graphdb.services.TransferredProcessInstanceService;
import at.ac.wuwien.causalminer.transformer.model.template.TemplateNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GraphDbTemplateViolationChecker {

    @Autowired
    private TransferredProcessInstanceService transferredProcessInstanceService;
    @Autowired
    private TemplateViolationServices templateViolationServices;

    private Map<UUID, TransferredProcessInstance> foundViolations = new HashMap<>();
    private Map<Long, ViolatingNode> violatingNodeMap = new HashMap<>();

    private Function<UUID, TransferredProcessInstance> getTransferredProcessInstance = instanceId -> {
        Optional<TransferredProcessInstance> optionalTransferredProcessInstance = transferredProcessInstanceService.findTransferredProcessInstanceByInstanceId(instanceId);
        return optionalTransferredProcessInstance.orElse(new TransferredProcessInstance(instanceId));
    };


    public void checkForViolations(DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph) {

        foundViolations = new HashMap<>();
        violatingNodeMap = new HashMap<>();

        violationOfTemporalOrder();

        violationOfStartCardinality(templateGraph);
        violationOfEndCardinality(templateGraph);

        violationOfAsymmetry(templateGraph);

        transferredProcessInstanceService.saveAll(foundViolations.values());

    }

    private void violationOfTemporalOrder() {
        List<InstanceRelationship> violatingRelations = templateViolationServices.findAllTemporalRelationshipViolations();
        addRelationshipViolationToProcessInstance(violatingRelations, ViolationType.Temporal);
    }

    private void violationOfEndCardinality(DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph) {

        List<CardinalityInputVariables> inputVariables = createCardinalInputVariables(templateGraph, false);
        List<EndCardinalityViolationsQueryResult> endCardinalityViolationsQueryResults = templateViolationServices.findAllEndCardinalityViolations(inputVariables);

        for (EndCardinalityViolationsQueryResult endCardinalityViolationsQueryResult : endCardinalityViolationsQueryResults) {

            TemplateNode currentTemplate = new TemplateNode(endCardinalityViolationsQueryResult.getSourceNodeType());
            TemplateNode relatedTemplate = new TemplateNode(endCardinalityViolationsQueryResult.getTargetNodeType());

            CardinalRelationship cardinalRelationship = templateGraph.getEdge(currentTemplate, relatedTemplate);
            if (cardinalRelationship != null && endCardinalityViolationsQueryResult.getCardinality() > cardinalRelationship.getCardinalityEnd()) {
                ViolatingNode violatingStartNode = new ViolatingNode(endCardinalityViolationsQueryResult.getSourceNodeId(), endCardinalityViolationsQueryResult.getSourceNodeType(), endCardinalityViolationsQueryResult.getSourceNodeType());
                Violation newViolation = new Violation(violatingStartNode, null, ViolationType.Cardinality);

                TransferredProcessInstance transferredProcessInstance = foundViolations.computeIfAbsent(UUID.fromString(endCardinalityViolationsQueryResult.getInstanceId()), getTransferredProcessInstance);
                transferredProcessInstance.getViolations().add(newViolation);
            }
        }
    }

    private void violationOfStartCardinality(DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph) {

        List<CardinalityInputVariables> inputVariables = createCardinalInputVariables(templateGraph, true);
        List<StartCardinalityViolationsQueryResult> startCardinalityViolationsQueryResults = templateViolationServices.findAllStartCardinalityViolations(inputVariables);

        for (StartCardinalityViolationsQueryResult startCardinalityViolationsQueryResult : startCardinalityViolationsQueryResults) {

            TemplateNode currentTemplate = new TemplateNode(startCardinalityViolationsQueryResult.getSourceNodeType());
            TemplateNode relatedTemplate = new TemplateNode(startCardinalityViolationsQueryResult.getTargetNodeType());

            CardinalRelationship cardinalRelationship = templateGraph.getEdge(relatedTemplate, currentTemplate);
            if (cardinalRelationship != null && startCardinalityViolationsQueryResult.getCardinality() > cardinalRelationship.getCardinalityStart()) {
                ViolatingNode violatingStartNode = new ViolatingNode(startCardinalityViolationsQueryResult.getTargetNodeId(), startCardinalityViolationsQueryResult.getTargetNodeType(), startCardinalityViolationsQueryResult.getTargetNodeType());
                Violation newViolation = new Violation(violatingStartNode, null, ViolationType.Cardinality);

                TransferredProcessInstance transferredProcessInstance = foundViolations.computeIfAbsent(UUID.fromString(startCardinalityViolationsQueryResult.getInstanceId()), getTransferredProcessInstance);
                transferredProcessInstance.getViolations().add(newViolation);
            }
        }
    }

    private List<CardinalityInputVariables> createCardinalInputVariables(DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph, Boolean getStartCardinality) {

        List<CardinalityInputVariables> inputVariables = new ArrayList<>();

        Set<CardinalRelationship> cardinalRelationships = templateGraph.edgeSet();

        for (CardinalRelationship cardinalRelationship : cardinalRelationships) {
            Integer cardinality;
            if (getStartCardinality) {
                cardinality = cardinalRelationship.getCardinalityStart();
            } else {
                cardinality = cardinalRelationship.getCardinalityEnd();
            }

            TemplateNode sourceNode = templateGraph.getEdgeSource(cardinalRelationship);
            TemplateNode targetNode = templateGraph.getEdgeTarget(cardinalRelationship);

            if (cardinality == Integer.MAX_VALUE) {
                inputVariables.add(CardinalityInputVariables.buildUnboundCardinality(sourceNode.getEventType(), targetNode.getEventType()));
            } else {
                inputVariables.add(CardinalityInputVariables.buildBoundCardinality(sourceNode.getEventType(), targetNode.getEventType(), cardinality));
            }

        }

        return inputVariables;
    }


    private void violationOfAsymmetry(DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph) {
        List<AsymmetricConflictInput> asymmetricConflictInputs = createAsymmetricConflictInput(templateGraph);
        List<IHomogeneousRelationship<InstanceActivity>> violatingRelations = templateViolationServices.findAllAsymmetricConflictViolations(asymmetricConflictInputs);
        addRelationshipViolationToProcessInstance(violatingRelations, ViolationType.Asymmetry);
    }

    private <V extends IHomogeneousRelationship<InstanceActivity>> void addRelationshipViolationToProcessInstance(List<V> violatingRelations, ViolationType violationType) {
        violatingRelations.forEach(violatingRelation -> {

            InstanceActivity startActivity = violatingRelation.getStartEvent();
            InstanceActivity endActivity = violatingRelation.getEndEvent();

            List<String> startActivityInstanceIds = new ArrayList<>();
            List<String> endActivityInstanceIds = new ArrayList<>();
            if(startActivity instanceof InstanceNullActivity) {
                startActivityInstanceIds = ((InstanceNullActivity)startActivity).getInstanceIds();
            }
            else {
                startActivityInstanceIds = ((InstanceIntermediateActivity)startActivity).getInstanceIds();
            }
            if(endActivity instanceof InstanceNullActivity) {
                endActivityInstanceIds = ((InstanceNullActivity)endActivity).getInstanceIds();
            }
            else {
                endActivityInstanceIds = ((InstanceIntermediateActivity)endActivity).getInstanceIds();
            }
            List<String> commonInstanceIds = startActivityInstanceIds.stream().filter(endActivityInstanceIds::contains).collect(Collectors.toList());

            ViolatingNode violatingStartNode = violatingNodeMap.computeIfAbsent(startActivity.getId(), k -> new ViolatingNode(startActivity.getId(), startActivity.getType(), startActivity.getType()));
            ViolatingNode violatingEndNode = violatingNodeMap.computeIfAbsent(endActivity.getId(), k -> new ViolatingNode(endActivity.getId(), endActivity.getType(), endActivity.getType()));
            Violation newViolation = new Violation(violatingStartNode, violatingEndNode, violationType);
            for (String instanceId : commonInstanceIds) {
                TransferredProcessInstance transferredProcessInstance = foundViolations.computeIfAbsent(UUID.fromString(instanceId), getTransferredProcessInstance);
                transferredProcessInstance.getViolations().add(newViolation);
            }
        });
    }

    private List<AsymmetricConflictInput> createAsymmetricConflictInput(DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph) {

        List<AsymmetricConflictInput> asymmetricConflictInputs = new ArrayList<>();

        templateGraph.edgeSet().stream().filter(CardinalRelationship::isAsymetric)
                .forEach(asymmetricEdge -> {
                    String sourceEventType = templateGraph.getEdgeSource(asymmetricEdge).getEventType();
                    String targetEventType = templateGraph.getEdgeTarget(asymmetricEdge).getEventType();
                    asymmetricConflictInputs.add(new AsymmetricConflictInput(sourceEventType, targetEventType));
                });

        return asymmetricConflictInputs;
    }


//    private boolean violationOfAsymmetry(DefaultDirectedImporterGraph<INode, DefaultEdge> mindedGraph, DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph, List<Violation> foundViolations) {
//
//        boolean foundAProblem = false;
//
//        Set<INode> nullNodes = mindedGraph.vertexSet().stream().filter(node -> node instanceof NullNode).collect(Collectors.toSet());
//
//        for (INode nullNode : nullNodes) {
//            Set<DefaultEdge> nullNodeOutgoingEdges = mindedGraph.outgoingEdgesOf(nullNode);
//
//            for (DefaultEdge nullNodeOutgoingEdge : nullNodeOutgoingEdges) {
//
//                INode source = mindedGraph.getEdgeSource(nullNodeOutgoingEdge);
//                INode target = mindedGraph.getEdgeTarget(nullNodeOutgoingEdge);
//                TemplateNode sourceTemplate = new TemplateNode(source.getEventType());
//                TemplateNode targetTemplate = new TemplateNode(target.getEventType());
//
//                CardinalRelationship cardinalRelationship = templateGraph.getEdge(sourceTemplate, targetTemplate);
//                if(!cardinalRelationship.isAsymetric()) {
////                    String violationText = "Asymmetric conflict violation between: " + source.getLabel() + " - " + target.getLabel();
//                    ViolatingNode violatingStartNode = new ViolatingNode(source.getLabel(), source.getEventType());
//                    ViolatingNode violatingEndNode = new ViolatingNode(target.getLabel(), target.getEventType());
//                    Violation newViolation = new Violation(violatingStartNode, violatingEndNode, ViolationType.Asymmetry);
//                    foundViolations.add(newViolation);
//                    foundAProblem = true;
//                }
//            }
//        }
//        return foundAProblem;
//    }

}
