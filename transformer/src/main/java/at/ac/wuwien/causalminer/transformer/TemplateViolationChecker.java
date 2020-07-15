package at.ac.wuwien.causalminer.transformer;

import at.ac.wuwien.causalminer.transformer.model.INode;
import at.ac.wuwien.causalminer.transformer.model.template.CardinalRelationship;
import at.ac.wuwien.causalminer.erp2graphdb.services.TransferredProcessInstanceService;
import at.ac.wuwien.causalminer.erp2graphdb.services.ViolationService;
import at.ac.wuwien.causalminer.transformer.model.template.TemplateNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Slf4j
public class TemplateViolationChecker {

    @Autowired
    private TransferredProcessInstanceService transferredProcessInstanceService;
    @Autowired
    private ViolationService violationService;

    @Transactional("erp2graphTransactionManager")
    public boolean checkForViolations(UUID instanceId, String rootNoteId, DefaultDirectedImporterGraph<INode, DefaultEdge> mindedGraph, DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph) {
        return false;
    }
//        Optional<TransferredProcessInstance> optionalTransferredProcessInstance = transferredProcessInstanceService.findTransferredProcessInstanceByInstanceId(instanceId);
//        TransferredProcessInstance transferredProcessInstance = optionalTransferredProcessInstance.orElse(new TransferredProcessInstance(instanceId, rootNoteId));
//
//        List<Violation> foundViolations = new ArrayList<>();
//        boolean problemFound = false;
//
//        // check cardinality
////        Iterator<TemplateNode> iterator = new DepthFirstIterator<>(templateGraph);
////        while (iterator.hasNext()) {
////            TemplateNode currentTemplateNode = iterator.next();
//
//            if (!violationOfStartCardinality(mindedGraph, templateGraph, foundViolations)) {
//                problemFound = true;
//            }
//            if (!violationOfEndCardinality(mindedGraph, templateGraph, foundViolations)) {
//                problemFound = true;
//            }
////        }
//
//        // check temporal order
//        if (!violationOfTemporalOrder(mindedGraph, foundViolations)) {
//            problemFound = true;
//        }
//
//        // check asymmetry
//        if (!violationOfAsymmetry(mindedGraph, templateGraph, foundViolations)) {
//            problemFound = true;
//        }
//
////        foundViolations.forEach(violation -> violation.setTransferredProcessInstance(transferredProcessInstance));
//        transferredProcessInstance.setViolations(foundViolations);
//        transferredProcessInstanceService.save(transferredProcessInstance);
//
//        return problemFound;
//    }
//
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
//
//    private boolean violationOfTemporalOrder(DefaultDirectedImporterGraph<INode, DefaultEdge> mindedGraph, List<Violation> foundViolations) {
//        boolean foundAProblem = false;
//
//        Set<DefaultEdge> edges = mindedGraph.edgeSet();
//
//        for (DefaultEdge edge : edges) {
//            INode source = mindedGraph.getEdgeSource(edge);
//            INode target = mindedGraph.getEdgeTarget(edge);
//
//            if(source.getActivityCreationTime() != null && target.getActivityCreationTime() != null &&
//                    source.getActivityCreationTime().isAfter(target.getActivityCreationTime())) {
////                String violationText = "Temporal order violation between: " + source.getLabel() + " - " + target.getLabel();
//                ViolatingNode violatingStartNode = new ViolatingNode(source.getLabel(), source.getEventType());
//                ViolatingNode violatingEndNode = new ViolatingNode(target.getLabel(), target.getEventType());
//                Violation newViolation = new Violation(violatingStartNode, violatingEndNode, ViolationType.Temporal);
//                foundViolations.add(newViolation);
//                foundAProblem = true;
//            }
//        }
//        return foundAProblem;
//    }
//
//    private boolean violationOfEndCardinality(DefaultDirectedImporterGraph<INode, DefaultEdge> mindedGraph, DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph, List<Violation> foundViolations) {
//        boolean foundAProblem = false;
//        for (INode node : mindedGraph.vertexSet()) {
//
//            Map<String, Integer> targetEventTypeRelationAmount = new HashMap<>();
//
//            Set<DefaultEdge> nodeOutgoingEdges = mindedGraph.outgoingEdgesOf(node);
//            for (DefaultEdge nodeOutgoingEdge : nodeOutgoingEdges) {
//                INode target = mindedGraph.getEdgeTarget(nodeOutgoingEdge);
//                int amount = targetEventTypeRelationAmount.computeIfAbsent(target.getEventType(), k -> 0) + 1;
//                targetEventTypeRelationAmount.put(target.getEventType(), amount);
//            }
//
//            for (Map.Entry<String, Integer> stringIntegerEntry : targetEventTypeRelationAmount.entrySet()) {
//                String targetEventType = stringIntegerEntry.getKey();
//
//                TemplateNode sourceTemplate = new TemplateNode(node.getEventType());
//                TemplateNode targetTemplate = new TemplateNode(targetEventType);
//
//                CardinalRelationship cardinalRelationship = templateGraph.getEdge(sourceTemplate, targetTemplate);
//                if(stringIntegerEntry.getValue() > cardinalRelationship.getCardinalityEnd()) {
////                    String violationText = "Violation in end cardinality between: " + node.getEventType() + " - " + targetEventType;
//                    ViolatingNode violatingStartNode = new ViolatingNode(node.getLabel(), node.getEventType());
//                    ViolatingNode violatingEndNode = new ViolatingNode("", targetEventType);
//                    Violation newViolation = new Violation(violatingStartNode, violatingEndNode, ViolationType.Cardinality);
//                    foundViolations.add(newViolation);
//                    foundAProblem = true;
//                }
//            }
//
//        }
//        return foundAProblem;
//    }
//
//    private boolean violationOfStartCardinality(DefaultDirectedImporterGraph<INode, DefaultEdge> mindedGraph, DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph, List<Violation> foundViolations) {
//
//        boolean foundAProblem = false;
//        for (INode node : mindedGraph.vertexSet()) {
//
//            Map<String, Integer> sourceEventTypeRelationAmount = new HashMap<>();
//
//            Set<DefaultEdge> nodeIncomingEdges = mindedGraph.incomingEdgesOf(node);
//            for (DefaultEdge nodeIncomingEdge : nodeIncomingEdges) {
//                INode source = mindedGraph.getEdgeSource(nodeIncomingEdge);
//                int amount = sourceEventTypeRelationAmount.computeIfAbsent(source.getEventType(), k -> 0) + 1;
//                sourceEventTypeRelationAmount.put(source.getEventType(), amount);
//            }
//
//            for (Map.Entry<String, Integer> stringIntegerEntry : sourceEventTypeRelationAmount.entrySet()) {
//                String sourceEventType = stringIntegerEntry.getKey();
//
//                TemplateNode sourceTemplate = new TemplateNode(sourceEventType);
//                TemplateNode targetTemplate = new TemplateNode(node.getEventType());
//
//                CardinalRelationship cardinalRelationship = templateGraph.getEdge(sourceTemplate, targetTemplate);
//                if(stringIntegerEntry.getValue() > cardinalRelationship.getCardinalityStart()) {
////                    String violationText = "Violation in start cardinality between: " + sourceEventType + " - " + node.getEventType();
//                    ViolatingNode violatingStartNode = new ViolatingNode("", sourceEventType);
//                    ViolatingNode violatingEndNode = new ViolatingNode(node.getLabel(), node.getEventType());
//                    Violation newViolation = new Violation(violatingStartNode, violatingEndNode, ViolationType.Cardinality);
//                    foundViolations.add(newViolation);
//                    foundAProblem = true;
//                }
//            }
//
//        }
//        return foundAProblem;
//    }

}
