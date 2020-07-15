package at.ac.wuwien.causalminer.transformer;

import at.ac.wuwien.causalminer.transformer.model.INode;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GraphToProcessInstanceConverter {

    @Value("${erp2graph.transformation.withNullEvents}")
    private boolean withNullEvents;
    @Value("${erp2graph.transformation.useCreationDate.if.changeDate.isNull}")
    private Boolean changeDateNullUseCreationDate;

    @Getter
    private Map<INode, InstanceActivity> eventMap = new HashMap<>();

    public void reset() {
        eventMap = new HashMap<>();
    }

    public void createAndAddEvents(Graph<INode, DefaultEdge> graph) throws Exception {

        Set<UUID> lastInstanceIds = new HashSet<>();
        Map<INode, InstanceActivity> localEventMap = new HashMap<>();
        try {

            Set<INode> vertexSet = graph.vertexSet();
            vertexSet.forEach(n -> {
                lastInstanceIds.clear();
                lastInstanceIds.addAll(n.getInstanceIds());
                if (eventMap.containsKey(n)) {

                    InstanceActivity alreadyExistingEvent = eventMap.get(n);
                    if (alreadyExistingEvent instanceof InstanceIntermediateActivity) {
                        Set<String> newInstanceIds = n.getInstanceIds().stream().map(UUID::toString).collect(Collectors.toSet());
                        newInstanceIds.addAll(((InstanceIntermediateActivity) alreadyExistingEvent).getInstanceIds());
                        ((InstanceIntermediateActivity) alreadyExistingEvent).getInstanceIds().clear();
                        ((InstanceIntermediateActivity) alreadyExistingEvent).getInstanceIds().addAll(newInstanceIds);
                    }

                    localEventMap.put(n, eventMap.get(n));
                } else {
                    localEventMap.put(n, n.getEventBuilder().getEvent(n.getInstanceIds(), changeDateNullUseCreationDate));
                }
            });

            Set<DefaultEdge> edgeSet = graph.edgeSet();
            for (DefaultEdge edge : edgeSet) {
                INode sourceNode = graph.getEdgeSource(edge);
                InstanceActivity sourceEvent = localEventMap.get(sourceNode);

                INode targetNode = graph.getEdgeTarget(edge);
                InstanceActivity targetEvent = localEventMap.get(targetNode);

                if (targetEvent != null && sourceEvent.getFollowUpRelationships().stream().noneMatch(path -> path.getStartEvent().equals(sourceEvent) && path.getEndEvent().equals(targetEvent))) {
                    if (sourceEvent instanceof InstanceNullActivity || targetEvent instanceof InstanceNullActivity) {
                        sourceEvent.getNullFollowUpRelationships().add(new NullInstanceRelationship(sourceEvent, targetEvent));
                    } else {
                        sourceEvent.getFollowUpRelationships().add(new InstanceRelationship(sourceEvent, targetEvent));
                    }
                }
            }


//            Set<INode> vertexSet = graph.vertexSet();
//            vertexSet.forEach(n -> {
//                lastInstanceIds.clear();
//                lastInstanceIds.addAll(n.getInstanceIds());
//                if (eventMap.containsKey(n)) {
//                    localEventMap.put(n, eventMap.get(n));
//                } else {
//                    localEventMap.put(n, n.getEventBuilder().getEvent(n.getInstanceIds(), changeDateNullUseCreationDate));
//                }
//            });
//
//            vertexSet.forEach(v -> {
//                InstanceActivity sourceEvent = localEventMap.get(v);
//                if (sourceEvent != null) {
//                    Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(v);
//
//                    for (DefaultEdge outgoingEdge : outgoingEdges) {
//                        INode targetNode = graph.getEdgeTarget(outgoingEdge);
//                        InstanceActivity targetEvent = localEventMap.get(targetNode);
//
//                        if (targetEvent != null && sourceEvent.getFollowUpRelationships().stream().noneMatch(path -> path.getStartEvent().equals(sourceEvent) && path.getEndEvent().equals(targetEvent))) {
//                            sourceEvent.getFollowUpRelationships().add(new InstanceRelationship(sourceEvent, targetEvent));
//                        }
//                    }
//                }
//            });

        } catch (Exception ex) {
            String instanceIdString = lastInstanceIds.stream().map(UUID::toString).collect(Collectors.joining(", "));
            log.error("Exception while converting the activity with the instance Ids: " + instanceIdString);
            throw ex;
        }

        eventMap.putAll(localEventMap);
    }

    public List<InstanceActivity> getInstanceActivities() {
        return new ArrayList<>(eventMap.values());
    }


}
