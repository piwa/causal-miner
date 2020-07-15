package at.ac.wuwien.causalminer.transformer;

import at.ac.wuwien.causalminer.transformer.model.EndNode;
import at.ac.wuwien.causalminer.transformer.model.INode;
import at.ac.wuwien.causalminer.transformer.model.NullNode;
import at.ac.wuwien.causalminer.transformer.model.StartNode;
import at.ac.wuwien.causalminer.transformer.model.template.CardinalRelationship;
import at.ac.wuwien.causalminer.transformer.model.template.TemplateNode;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.erp2graphdb.services.TransferredProcessInstanceService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@ToString
@Component
@Slf4j
public class GraphBasedTransformer {

    @Autowired
    private TemplateViolationChecker templateViolationChecker;
    @Autowired
    private TransferredProcessInstanceService transferredProcessInstanceService;

    @Value("${erp2graph.transformation.withNullEvents}")
    private boolean withNullEvents;
    @Value("${erp2graph.transformation.withIntervalFromCreationDateToChangeDate}")
    private boolean withIntervalFromCreationDateToChangeDate;
    @Value("${erp2graph.transformation.withTimeFrameForCreationDate}")
    private boolean withTimeFrameForCreationDate;
    @Value("${erp2graph.transformation.withTimeFrameForCreationDate.timeFrame}")
    private Integer creationDateTimeFrame;
    @Value("${erp2graph.transformation.useCreationDate.if.changeDate.isNull}")
    private Boolean useCreationDateIfChangeDateNull;

    @Value("${erp2graph.transformation.rendergraph}")
    private Boolean renderGraph;
    @Value("${erp2graph.transformation.rendergraph.path}")
    private String svgRenderPath;

    @Getter
    @Setter
    private Graph<INode, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

    @Getter
    private Map<Integer, InstanceActivity> eventMap = new HashMap<>();
    private List<InstanceActivity> nullEvents = new ArrayList<>();

    public void resetProperties() {

        transferredProcessInstanceService.deleteAll();

        graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        eventMap = new HashMap<>();
        nullEvents = new ArrayList<>();
    }

    public Graph<INode, DefaultEdge> transform(INode rootNote, UUID instanceId, DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph, StartNode startNode, EndNode endNode) {
        return transform(rootNote, instanceId, new DefaultDirectedImporterGraph<>(DefaultEdge.class), templateGraph, startNode, endNode);
    }

    public Graph<INode, DefaultEdge> transform(INode rootNote, UUID instanceId, DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph, DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph, StartNode startNode, EndNode endNode) {

        DefaultDirectedImporterGraph<INode, DefaultEdge> databaseGraph = new DefaultDirectedImporterGraph<>(DefaultEdge.class);
        createDatabaseGraph(rootNote, databaseGraph, instanceId);

        if (renderGraph) {
            databaseGraph.saveSvg(svgRenderPath + "databaseGraph");
            templateGraph.saveSvg(svgRenderPath + "templateGraph");
        }

        Set<String> eventTypesInTemplateGraph = templateGraph.vertexSet().stream().map(TemplateNode::getEventType).collect(Collectors.toSet());
        databaseGraph.vertexSet().stream().filter(node -> eventTypesInTemplateGraph.contains(node.getEventType())).forEach(finalGraph::addVertex);

        Iterator<TemplateNode> iterator = new DepthFirstIterator<>(templateGraph);
        while (iterator.hasNext()) {
            TemplateNode currentTemplateNode = iterator.next();

            List<TemplateNode> parentTemplateNodes = Graphs.predecessorListOf(templateGraph, currentTemplateNode);
            if (parentTemplateNodes.size() > 0) {           // TODO This if can be deleted
                for (TemplateNode parentTemplateNode : parentTemplateNodes) {
                    List<INode> finalGraphParentNodesOfType = findAllNodesByTypeInGraph(parentTemplateNode.getEventType(), finalGraph);
                    List<INode> finalGraphCurrentNodesOfType = findAllNodesByTypeInGraph(currentTemplateNode.getEventType(), finalGraph);       // TODO this can be done before the for loop
                    addEdges(finalGraph, databaseGraph, finalGraphParentNodesOfType, finalGraphCurrentNodesOfType, rootNote);
                }
            }

            if (renderGraph) {
                finalGraph.saveSvg(svgRenderPath + "finalGraph");
            }

        }

        addStartAndEndNodes(finalGraph, startNode, endNode);

        removeNullNodes(finalGraph);

        return finalGraph;
    }

    private void removeNullNodes(DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph) {

        DefaultDirectedImporterGraph<INode, DefaultEdge> backupGraph = (DefaultDirectedImporterGraph<INode, DefaultEdge>) finalGraph.clone();

        removeNullNodesRecursive(finalGraph);
        removeNullNodesExceptOfOneRecursive(backupGraph);
        Graphs.addGraph(finalGraph, backupGraph);

    }

    private void removeNullNodesRecursive(DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph) {

        Optional<INode> optionalNullNode = finalGraph.vertexSet().stream().filter(node -> node instanceof NullNode).findAny();

        if (optionalNullNode.isEmpty()) {
            return;
        }

        INode nullNode = optionalNullNode.get();
        List<INode> predecessor = Graphs.predecessorListOf(finalGraph, nullNode);
        List<INode> successor = Graphs.successorListOf(finalGraph, nullNode);

        predecessor.forEach(parent -> successor.forEach(child -> finalGraph.addEdge(parent, child)));
        finalGraph.removeVertex(nullNode);

        removeNullNodesRecursive(finalGraph);
    }

    private void removeNullNodesExceptOfOneRecursive(DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph) {

        Optional<INode> optionalNullNode = finalGraph.vertexSet().stream().filter(node ->
            node instanceof NullNode && Graphs.predecessorListOf(finalGraph, node).stream().anyMatch(pre -> pre instanceof NullNode)
        ).findAny();

        if (optionalNullNode.isEmpty()) {
            return;
        }

        INode nullNode = optionalNullNode.get();
        List<INode> predecessor = Graphs.predecessorListOf(finalGraph, nullNode);
        List<INode> successor = Graphs.successorListOf(finalGraph, nullNode);

        predecessor.forEach(parent -> successor.forEach(child -> finalGraph.addEdge(parent, child)));
        finalGraph.removeVertex(nullNode);

        removeNullNodesExceptOfOneRecursive(finalGraph);
    }

    private void addStartAndEndNodes(DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph, StartNode startNode, EndNode endNode) {
        Set<INode> rootNotes = new HashSet<>();
        Set<INode> leafNotes = new HashSet<>();

        finalGraph.vertexSet().forEach(node -> {
            if (finalGraph.inDegreeOf(node) == 0) {
                rootNotes.add(node);
            }
            if (finalGraph.outDegreeOf(node) == 0) {
                leafNotes.add(node);
            }
        });

        startNode.getChildren().addAll(rootNotes);
        endNode.getParents().addAll(leafNotes);
        finalGraph.addVertex(startNode);
        finalGraph.addVertex(endNode);
        rootNotes.forEach(node -> finalGraph.addEdge(startNode, node));
        leafNotes.forEach(node -> finalGraph.addEdge(node, endNode));
    }

    private void addEdges(Graph<INode, DefaultEdge> finalGraph, DefaultDirectedImporterGraph<INode, DefaultEdge> databaseGraph, List<INode> finalGraphParentNodesOfType, List<INode> finalGraphCurrentNodesOfType, INode rootNode) {
        AllDirectedPaths<INode, DefaultEdge> allDirectedPaths = new AllDirectedPaths<>(databaseGraph);

        Map<INode, List<GraphPath<INode, DefaultEdge>>> parentNodePaths = new HashMap<>();
        for (INode parentNode : finalGraphParentNodesOfType) {
            parentNodePaths.put(parentNode, allDirectedPaths.getAllPaths(rootNode, parentNode, false, 20));
        }

        Map<INode, List<GraphPath<INode, DefaultEdge>>> currentNodePaths = new HashMap<>();
        for (INode currentNode : finalGraphCurrentNodesOfType) {
            currentNodePaths.put(currentNode, allDirectedPaths.getAllPaths(rootNode, currentNode, false, 20));
        }

        for (Map.Entry<INode, List<GraphPath<INode, DefaultEdge>>> iNodeListEntry : currentNodePaths.entrySet()) {

            boolean relationshipWasAdded = false;

            for (GraphPath<INode, DefaultEdge> pathToChild : iNodeListEntry.getValue()) {
                GraphPath<INode, DefaultEdge> bestFit = null;
                int bestFitLength = 0;
                for (List<GraphPath<INode, DefaultEdge>> value : parentNodePaths.values()) {
                    for (GraphPath<INode, DefaultEdge> pathToParent : value) {
                        int similarElements = getDifferenceAmount(pathToParent, pathToChild);
                        if (bestFit == null || similarElements > bestFitLength) {
                            bestFit = pathToParent;
                            bestFitLength = similarElements;
                        }
                    }
                }

                if (bestFit != null) {
                    finalGraph.addEdge(bestFit.getEndVertex(), iNodeListEntry.getKey());
                    relationshipWasAdded = true;
                }
            }
            if (!relationshipWasAdded) {
                finalGraphParentNodesOfType.forEach(parentNode -> finalGraph.addEdge(parentNode, iNodeListEntry.getKey()));
            }
        }
    }

    private int getDifferenceAmount(GraphPath<INode, DefaultEdge> pathToParent, GraphPath<INode, DefaultEdge> pathToChild) {
        List<DefaultEdge> intersect = pathToParent.getEdgeList().stream()
                .filter(pathToChild.getEdgeList()::contains)
                .collect(Collectors.toList());
        return intersect.size();
    }

    // TODO this method might be faster by using a Map that is filled before
    private List<INode> findAllNodesByTypeInGraph(String type, DefaultDirectedImporterGraph<INode, DefaultEdge> databaseGraph) {
        List<INode> nodes = databaseGraph.vertexSet().stream()
                .filter(node -> node.getEventType().equals(type))
                .collect(Collectors.toList());
        return nodes;
    }

    private void createDatabaseGraph(INode node, Graph<INode, DefaultEdge> databaseGraph, UUID instanceId) {
        if (node.getInstanceIds().contains(instanceId)) {
            databaseGraph.addVertex(node);
            node.getParents().stream()
                    .filter(n -> n.getInstanceIds().contains(instanceId))
                    .forEach(parent -> {
                        databaseGraph.addVertex(parent);
                        databaseGraph.addEdge(parent, node);
                    });
        }
        node.getChildren().stream()
                .filter(n -> n.getInstanceIds().contains(instanceId))
                .forEach(child -> createDatabaseGraph(child, databaseGraph, instanceId));

    }

}

