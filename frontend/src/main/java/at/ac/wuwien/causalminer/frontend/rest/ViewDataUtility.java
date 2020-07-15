package at.ac.wuwien.causalminer.frontend.rest;

import at.ac.wuwien.causalminer.frontend.model.cytoscape.*;
import at.ac.wuwien.causalminer.frontend.model.tables.DurationTableEntry;
import at.ac.wuwien.causalminer.frontend.model.tables.ProcessInstanceViolationsTableEntry;
import at.ac.wuwien.causalminer.frontend.model.tables.ProcessViewTableEntry;
import at.ac.wuwien.causalminer.frontend.configuration.NodeStyleProperties;
import at.ac.wuwien.causalminer.neo4jdb.domain.IHomogeneousRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.INode;
import at.ac.wuwien.causalminer.neo4jdb.domain.IRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceNullActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.*;
import at.ac.wuwien.causalminer.frontend.model.tables.ConnectedInstanceAmountTableEntry;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.EventTypeResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.TotalDurationsQueryResult;
import at.ac.wuwien.causalminer.erp2graphdb.model.Violation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ViewDataUtility {

    @Autowired
    private NodeStyleProperties nodeStyleProperties;

    @Value("${frontend.show.startAndEndNodes}")
    private boolean showStartAndEndNodes = false;

    public static Predicate<INode<?>> filterNoStartEndActivities = INode::isEssentialForProcessDepiction;
    public static Predicate<INode<?>> filterNoNullActivities = t -> t instanceof InstanceNullActivity;
    public static Predicate<INode<?>> filterNoStartEndNullActivities = filterNoStartEndActivities.or(filterNoNullActivities);
    public static Predicate<INode<?>> unfilteredActivities = x -> true;

    public static Predicate<IRelationship<?, ?>> filterRelationshipNoStartEndActivities = rel -> rel.getEndEvent().isEssentialForProcessDepiction();
    public static Predicate<IRelationship<?, ?>> filterRelationshipNoNullActivities = t -> t instanceof InstanceNullActivity;
    public static Predicate<IRelationship<?, ?>> filterRelationshipNoStartEndNullActivities = filterRelationshipNoStartEndActivities.or(filterRelationshipNoNullActivities);
    public static Predicate<IRelationship<?, ?>> unfilteredRelationships = x -> true;

    // TODO make it configurable in the frontend
    public <T extends INode<V>, V extends IHomogeneousRelationship<T>> CytoscapeElement createStartEndFilteredViewGraph(List<T> persistedGraph) {
        return createStartEndFilteredViewGraph(persistedGraph, new ArrayList<>());
    }

    public <T extends INode<V>, V extends IHomogeneousRelationship<T>> CytoscapeElement createStartEndFilteredViewGraph(List<T> persistedGraph, List<String> nodeIdsToAnnotated) {
        if (showStartAndEndNodes) {
            return createFilteredViewGraph(persistedGraph, nodeIdsToAnnotated, unfilteredActivities, unfilteredRelationships);
        } else {
            return createFilteredViewGraph(persistedGraph, nodeIdsToAnnotated, filterNoStartEndActivities, filterRelationshipNoStartEndActivities);
        }

    }

    private <T extends INode<V>, V extends IHomogeneousRelationship<T>> CytoscapeElement createFilteredViewGraph(List<T> persistedGraph, List<String> nodeIdsToAnnotated, Predicate<INode<?>> activityFilterOperation, Predicate<IRelationship<?, ?>> relationshipFilterOperation) {

        CytoscapeElement cytoscapeElement = new CytoscapeElement();

        Set<Long> ids = persistedGraph.stream().map(INode::getId).collect(Collectors.toSet()); // TODO kind of workaround because the aggregated nodes do not have an ID

        persistedGraph.stream().filter(activityFilterOperation).forEach(persistedNode -> {

            if (persistedNode.getId() == null) {             // TODO kind of workaround because the aggregated nodes do not have an ID
                long newId = 0L;
                do {
                    newId = UUID.randomUUID().getLeastSignificantBits();
                } while (ids.contains(newId));
                persistedNode.setId(newId);
            }

            CytoscapeNodeData viewNodeData = new CytoscapeNodeData(String.valueOf(persistedNode.getId()), persistedNode.getType(), persistedNode.getType());
            if(persistedNode.isAggregated()) {
                viewNodeData.setLabel(getLabel(persistedNode) + "\n(" + persistedNode.getCount() + ")");
            }
            else {
                viewNodeData.setLabel(getLabel(persistedNode));
            }
            viewNodeData.setNodeType(persistedNode.getType());
            viewNodeData.setColor(getColor(persistedNode));
            viewNodeData.addLabels(persistedNode.getLabels());
            viewNodeData.setProperties(persistedNode.getProperties());

//            if (persistedNode instanceof InstanceNullActivity) {
//                viewNodeData.setShape("circle");
//            }

            if(nodeIdsToAnnotated.contains(String.valueOf(persistedNode.getId()))) {
                viewNodeData.setBorderColor("#FF0000");
                viewNodeData.setBorderWidth(6);
            }

            cytoscapeElement.getNodes().add(new CytoscapeNode(viewNodeData));
        });

        persistedGraph.stream().filter(activityFilterOperation).forEach(persistedNode -> {
            persistedNode.getFollowUpRelationships().stream().filter(relationshipFilterOperation).forEach(persistedRel -> {
                String startUuid = String.valueOf(persistedRel.getStartEvent().getId());
                String endUuid = String.valueOf(persistedRel.getEndEvent().getId());
                CytoscapeEdgeData viewEdge = new CytoscapeEdgeData(startUuid, endUuid, persistedRel.isAsymmetricConflictRelationship());
                viewEdge.setLabel(persistedRel.getLabel());

                if(persistedRel.isAggregated()) {
                    viewEdge.setLabel(persistedRel.getLabel() + "\n(" + persistedRel.getCount() + ")");
                }
                else {
                    viewEdge.setLabel(persistedRel.getLabel());
                }

                viewEdge.setProperties(persistedRel.getProperties());
                cytoscapeElement.getEdges().add(new CytoscapeEdge(viewEdge));
            });
        });

        return cytoscapeElement;
    }


    private <T extends IHomogeneousRelationship<?>> String getColor(INode<T> event) {
        String key = event.getType().toLowerCase();
        return nodeStyleProperties.getColor().getOrDefault(key, nodeStyleProperties.getDefaultColor());
    }

    private <T extends IHomogeneousRelationship<?>> String getLabel(INode<T> event) {
        String key = event.getType().toLowerCase();
        return nodeStyleProperties.getLabel().getOrDefault(key, key);
    }

    public List<ProcessViewTableEntry> getProcessViewTableEntries(Collection<String> instanceIdStrings) {
        List<ProcessViewTableEntry> caseTableEntries = new ArrayList<>();
        instanceIdStrings.forEach(c -> addProcessViewTableEntry(caseTableEntries, c, ""));
        return caseTableEntries;
    }

    public List<ProcessViewTableEntry> getInstanceTableEntries(Stream<InstanceStartActivity> activityNodes, String idName) {
        List<ProcessViewTableEntry> caseTableEntries = new ArrayList<>();
        activityNodes.forEach(c -> addProcessViewTableEntry(caseTableEntries, c.getInstanceId(), c.getOrigIds().get(idName)));
        return caseTableEntries;
    }

    private void addProcessViewTableEntry(List<ProcessViewTableEntry> instanceTableEntries, String instanceId, String origId) {
        ProcessViewTableEntry processViewTableEntry = new ProcessViewTableEntry();
        String idStart = instanceId.substring(0, 13);
        String idEnd = instanceId.substring(20, instanceId.length() - 1);
        String shortId = idStart + "..." + idEnd;
        processViewTableEntry.setInstanceIdFull(instanceId);
        processViewTableEntry.setOrigId(origId);        // TODO use an enum for this?
//        processViewTableEntry.setFirmNr(origIds.get("FirmNr"));
        instanceTableEntries.add(processViewTableEntry);
    }

    public List<ConnectedInstanceAmountTableEntry> getConnectedInstanceAmountTableEntries(Stream<EventTypeResult> activityNodes, String eventType) {
        List<ConnectedInstanceAmountTableEntry> caseTableEntries = new ArrayList<>();
        activityNodes.forEach(c -> {
            ConnectedInstanceAmountTableEntry entry = new ConnectedInstanceAmountTableEntry();
            entry.setOrigId(c.getOrigId());
            entry.setAmount(c.getConnectedInstanceAmount());
            entry.setEventType(eventType);
            caseTableEntries.add(entry);
        });
        return caseTableEntries;
    }

    public List<ProcessInstanceViolationsTableEntry> getProcessInstanceViolationsTableEntries(List<Violation> violations) {
        List<ProcessInstanceViolationsTableEntry> caseTableEntries = new ArrayList<>();
        violations.forEach(c -> {
            ProcessInstanceViolationsTableEntry entry = new ProcessInstanceViolationsTableEntry();

            entry.setViolationType(c.getViolationType().toString());
            entry.setViolationText("violation");
            entry.setStartNodeLabel(c.getViolationStartNode().getEventType());
            if(c.getViolationEndNode() != null) {
                entry.setEndNodeLabel(c.getViolationEndNode().getEventType());
            }
            caseTableEntries.add(entry);
        });
        return caseTableEntries;
    }

    public List<DurationTableEntry> getDurationTableEntries(Stream<TotalDurationsQueryResult> totalDurationsResult) {
        List<DurationTableEntry> durationTableEntries = new ArrayList<>();
        totalDurationsResult.forEach(durationsResult -> {
            DurationTableEntry durationTableEntry = new DurationTableEntry();
            String instanceId = durationsResult.getStartNode().getInstanceId();
            String idStart = instanceId.substring(0, 13);
            String idEnd = instanceId.substring(20, instanceId.length() - 1);
            String shortId = idStart + "..." + idEnd;

            durationTableEntry.setInstanceIdFull(instanceId);
            durationTableEntry.setInstanceId(shortId);
            durationTableEntry.setEndEventType(durationsResult.getEndNode().getType());
            durationTableEntry.setPathEndDate(durationsResult.getEndNode().getTimestamp().toString());
            durationTableEntry.setPathStartDate(durationsResult.getStartNode().getTimestamp().toString());

            if (durationsResult.getTotalDuration() == null) {
                durationTableEntry.setTotalDurationMinutes("null");
            } else {

                Double totalDuration = ((double) durationsResult.getTotalDuration()) / 1000 / 60 / 60 / 24;
                DecimalFormat df = new DecimalFormat("#.00");
                durationTableEntry.setTotalDurationMinutes(df.format(totalDuration));
            }

            durationTableEntries.add(durationTableEntry);
        });
        return durationTableEntries;
    }

    public <T extends INode<?>> void removeStartAndEndNode(List<T> neo4jResult) {
        neo4jResult.removeIf(activity -> activity.getType().toLowerCase().contains("start") || activity.getType().toLowerCase().contains("end"));

        neo4jResult.forEach(activity ->
                activity.getFollowUpRelationships().removeIf(relationship -> relationship.getEndEvent().getType().toLowerCase().contains("end"))
        );

    }
}
