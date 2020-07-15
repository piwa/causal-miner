package at.ac.wuwien.causalminer.transformer;

import at.ac.wuwien.causalminer.transformer.model.EndNode;
import at.ac.wuwien.causalminer.transformer.model.INode;
import at.ac.wuwien.causalminer.transformer.model.StartNode;
import at.ac.wuwien.causalminer.transformer.model.template.CardinalRelationship;
import at.ac.wuwien.causalminer.transformer.model.Node;
import at.ac.wuwien.causalminer.transformer.model.*;
import at.ac.wuwien.causalminer.transformer.model.template.TemplateNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultEdge;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(classes = TestApplicationContext.class)
@ActiveProfiles("test")
@Import(TestConfiguration.class)
@Slf4j
class GraphBasedTransformerTest_TwoGraphs {

    private Node node3a;
    private Node node4a;

    private Node node_1_0;
    private Node node_1_1;
    private Node node_1_2;
    private Node node_1_3b;
    private Node node_1_4b;
    private Node node_1_5;
    private Node node_1_6;

    private Node node_2_0;
    private Node node_2_1;
    private Node node_2_2;
    private Node node_2_3b;
    private Node node_2_4b;
    private Node node_2_5;
    private Node node_2_6;

    private TemplateNode templateNode0;
    private TemplateNode templateNode1;
    private TemplateNode templateNode2;
    private TemplateNode templateNode3;
    private TemplateNode templateNode4;
    private TemplateNode templateNode5;
    private TemplateNode templateNode6;

    @Autowired
    private GraphBasedTransformer graphBasedTransformer;

    private StartNode startNode_1;
    private StartNode startNode_2;
    private EndNode endNode_1;
    private EndNode endNode_2;

    private List<INode> nodeList_1 = new ArrayList<>();
    private List<INode> nodeList_2 = new ArrayList<>();
    private List<TemplateNode> templateNodeList = new ArrayList<>();

    private DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph;
    private DefaultDirectedTestGraph<INode, DefaultEdge> testGraph_1;
    private DefaultDirectedTestGraph<INode, DefaultEdge> testGraph_2;

    private UUID instanceId_1;
    private UUID instanceId_2;

    @BeforeEach
    public void TimeSetting1() {

        instanceId_1 = UUID.randomUUID();
        instanceId_2 = UUID.randomUUID();
        startNode_1 = StartAndEndNodeUtility.createStartNode(instanceId_1, new HashMap<>());
        startNode_2 = StartAndEndNodeUtility.createStartNode(instanceId_2, new HashMap<>());
        endNode_1 = StartAndEndNodeUtility.createEndNode(startNode_1.getInstanceId());
        endNode_2 = StartAndEndNodeUtility.createEndNode(startNode_2.getInstanceId());

        DateTime startDateTime = new DateTime(0);
        DateTime endDateTime = (new DateTime(0)).plusHours(1);
        node3a = new Node("3a", "3", startDateTime, endDateTime, null);
        node4a = new Node("4a", "4", startDateTime, endDateTime, null);

        node_1_0 = new Node("1_0", "0", startDateTime, endDateTime, null);
        node_1_1 = new Node("1_1", "1", startDateTime, endDateTime, null);
        node_1_2 = new Node("1_2", "2", startDateTime, endDateTime, null);
        node_1_3b = new Node( "1_3b", "3", startDateTime, endDateTime, null);
        node_1_4b = new Node( "1_4b", "4", startDateTime, endDateTime, null);
        node_1_5 = new Node( "1_5", "5", startDateTime, endDateTime, null);
        node_1_6 = new Node( "1_6", "6", startDateTime, endDateTime, null);
        nodeList_1.add(node_1_0);
        nodeList_1.add(node_1_1);
        nodeList_1.add(node_1_2);
        nodeList_1.add(node3a);
        nodeList_1.add(node_1_3b);
        nodeList_1.add(node4a);
        nodeList_1.add(node_1_4b);
        nodeList_1.add(node_1_5);
        nodeList_1.add(node_1_6);
        nodeList_1.forEach(node -> node.getInstanceIds().add(instanceId_1));

        addNodeRelationship(node_1_0, node_1_1);
        addNodeRelationship(node_1_1, node_1_2);
        addNodeRelationship(node_1_2, node3a);
        addNodeRelationship(node_1_2, node_1_3b);
        addNodeRelationship(node3a, node4a);
        addNodeRelationship(node_1_3b, node_1_4b);
        addNodeRelationship(node4a, node_1_5);
        addNodeRelationship(node_1_4b, node_1_5);
        addNodeRelationship(node_1_5, node_1_6);

        testGraph_1 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        nodeList_1.forEach(testGraph_1::addVertex);
        testGraph_1.addVertex(startNode_1);
        testGraph_1.addVertex(endNode_1);

        node_2_0 = new Node("2_0", "0", startDateTime, endDateTime, null);
        node_2_1 = new Node("2_1", "1", startDateTime, endDateTime, null);
        node_2_2 = new Node( "2_2", "2", startDateTime, endDateTime, null);
        node_2_3b = new Node( "2_3b", "3", startDateTime, endDateTime, null);
        node_2_4b = new Node( "2_4b", "4", startDateTime, endDateTime, null);
        node_2_5 = new Node( "2_5", "5", startDateTime, endDateTime, null);
        node_2_6 = new Node( "2_6", "6", startDateTime, endDateTime, null);
        nodeList_2.add(node_2_0);
        nodeList_2.add(node_2_1);
        nodeList_2.add(node_2_2);
        nodeList_2.add(node3a);
        nodeList_2.add(node_2_3b);
        nodeList_2.add(node4a);
        nodeList_2.add(node_2_4b);
        nodeList_2.add(node_2_5);
        nodeList_2.add(node_2_6);
        nodeList_2.forEach(node -> node.getInstanceIds().add(instanceId_2));

        addNodeRelationship(node_2_0, node_2_1);
        addNodeRelationship(node_2_1, node_2_2);
        addNodeRelationship(node_2_2, node3a);
        addNodeRelationship(node_2_2, node_2_3b);
        addNodeRelationship(node3a, node4a);
        addNodeRelationship(node_2_3b, node_2_4b);
        addNodeRelationship(node4a, node_2_5);
        addNodeRelationship(node_2_4b, node_2_5);
        addNodeRelationship(node_2_5, node_2_6);

        testGraph_2 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        nodeList_2.forEach(testGraph_2::addVertex);
        testGraph_2.addVertex(startNode_2);
        testGraph_2.addVertex(endNode_2);

        templateNode0 = new TemplateNode("0");
        templateNode1 = new TemplateNode("1");
        templateNode2 = new TemplateNode("2");
        templateNode3 = new TemplateNode("3");
        templateNode4 = new TemplateNode("4");
        templateNode5 = new TemplateNode("5");
        templateNode6 = new TemplateNode("6");
        templateNodeList.add(templateNode0);
        templateNodeList.add(templateNode1);
        templateNodeList.add(templateNode2);
        templateNodeList.add(templateNode3);
        templateNodeList.add(templateNode4);
        templateNodeList.add(templateNode5);
        templateNodeList.add(templateNode6);

        templateGraph = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        templateNodeList.forEach(templateGraph::addVertex);


    }

    private void addNodeRelationship(INode source, INode target) {
        source.getChildren().add(target);
        target.getParents().add(source);
    }

    @Test
    void transformation_sequentialTemplate_1() {

        templateGraph.addEdge(templateNode0, templateNode1);
        templateGraph.addEdge(templateNode1, templateNode2);
        templateGraph.addEdge(templateNode2, templateNode3);
        templateGraph.addEdge(templateNode3, templateNode4);
        templateGraph.addEdge(templateNode4, templateNode5);
        templateGraph.addEdge(templateNode5, templateNode6);

        testGraph_1.addEdge(startNode_1, node_1_0);
        testGraph_1.addEdge(node_1_0, node_1_1);
        testGraph_1.addEdge(node_1_1, node_1_2);
        testGraph_1.addEdge(node_1_2, node3a);
        testGraph_1.addEdge(node_1_2, node_1_3b);
        testGraph_1.addEdge(node3a, node4a);
        testGraph_1.addEdge(node_1_3b, node_1_4b);
        testGraph_1.addEdge(node4a, node_1_5);
        testGraph_1.addEdge(node_1_4b, node_1_5);
        testGraph_1.addEdge(node_1_5, node_1_6);
        testGraph_1.addEdge(node_1_6, endNode_1);

        testGraph_2.addEdge(startNode_2, node_2_0);
        testGraph_2.addEdge(node_2_0, node_2_1);
        testGraph_2.addEdge(node_2_1, node_2_2);
        testGraph_2.addEdge(node_2_2, node3a);
        testGraph_2.addEdge(node_2_2, node_2_3b);
        testGraph_2.addEdge(node3a, node4a);
        testGraph_2.addEdge(node_2_3b, node_2_4b);
        testGraph_2.addEdge(node4a, node_2_5);
        testGraph_2.addEdge(node_2_4b, node_2_5);
        testGraph_2.addEdge(node_2_5, node_2_6);
        testGraph_2.addEdge(node_2_6, endNode_2);

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph_1 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node_1_0, instanceId_1, finalGraph_1, templateGraph, startNode_1, endNode_1);
        assertEquals(testGraph_1, finalGraph_1, "Something is wrong transformedGraph: \n" + finalGraph_1.toString() + "\ntestGraph: \n" + testGraph_1.toString());

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph_2 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node_2_0, instanceId_2, finalGraph_2, templateGraph, startNode_2, endNode_2);
        assertEquals(testGraph_2, finalGraph_2, "Something is wrong transformedGraph: \n" + finalGraph_2.toString() + "\ntestGraph: \n" + testGraph_2.toString());

        checkIfTheRightAmountOfNodesAreInBothGraphs(2, finalGraph_1, finalGraph_2);

        Graph<INode, DefaultEdge> combinedGraph = (Graph<INode, DefaultEdge>) ((AbstractBaseGraph)finalGraph_1).clone();
        Graphs.addGraph(combinedGraph, finalGraph_1);
        Graphs.addGraph(combinedGraph, finalGraph_2);

        checkSuccessorOrPredecessorAmount(0, combinedGraph, Graphs::successorListOf, endNode_1, endNode_2);
        checkSuccessorOrPredecessorAmount(1, combinedGraph, Graphs::successorListOf, node3a, node_1_0, node_1_1, node_1_3b, node_1_4b, node_1_5, node_2_0, node_2_1, node_2_3b, node_2_4b, node_2_5, node_1_6, node_2_6);
        checkSuccessorOrPredecessorAmount(2, combinedGraph, Graphs::successorListOf, node_1_2, node4a, node_2_2);

        checkSuccessorOrPredecessorAmount(0, combinedGraph, Graphs::predecessorListOf, startNode_1, startNode_2);
        checkSuccessorOrPredecessorAmount(1, combinedGraph, Graphs::predecessorListOf, node_1_0, node_2_0, node4a, node_1_1, node_1_2, node_1_3b, node_1_4b, node_1_6, node_2_1, node_2_2, node_2_3b, node_2_4b, node_2_6);
        checkSuccessorOrPredecessorAmount(2, combinedGraph, Graphs::predecessorListOf, node_1_5, node3a, node_2_5);

    }

    @Test
    void transformation_sequentialTemplate_2() {

        templateGraph.addEdge(templateNode0, templateNode1);
        templateGraph.addEdge(templateNode1, templateNode4);
        templateGraph.addEdge(templateNode4, templateNode2);
        templateGraph.addEdge(templateNode2, templateNode3);
        templateGraph.addEdge(templateNode3, templateNode5);
        templateGraph.addEdge(templateNode5, templateNode6);

        testGraph_1.addEdge(startNode_1, node_1_0);
        testGraph_1.addEdge(node_1_0,  node_1_1);
        testGraph_1.addEdge(node_1_1,  node4a);
        testGraph_1.addEdge(node_1_1,  node_1_4b);
        testGraph_1.addEdge(node4a,    node_1_2);
        testGraph_1.addEdge(node_1_4b, node_1_2);
        testGraph_1.addEdge(node_1_2,  node3a);
        testGraph_1.addEdge(node_1_2,  node_1_3b);
        testGraph_1.addEdge(node3a,    node_1_5);
        testGraph_1.addEdge(node_1_3b, node_1_5);
        testGraph_1.addEdge(node_1_5,  node_1_6);
        testGraph_1.addEdge(node_1_6, endNode_1);

        testGraph_2.addEdge(startNode_2, node_2_0);
        testGraph_2.addEdge(node_2_0,  node_2_1);
        testGraph_2.addEdge(node_2_1,  node4a);
        testGraph_2.addEdge(node_2_1,  node_2_4b);
        testGraph_2.addEdge(node4a,    node_2_2);
        testGraph_2.addEdge(node_2_4b, node_2_2);
        testGraph_2.addEdge(node_2_2,  node3a);
        testGraph_2.addEdge(node_2_2,  node_2_3b);
        testGraph_2.addEdge(node3a,    node_2_5);
        testGraph_2.addEdge(node_2_3b, node_2_5);
        testGraph_2.addEdge(node_2_5,  node_2_6);
        testGraph_2.addEdge(node_2_6, endNode_2);

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph_1 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node_1_0, instanceId_1, finalGraph_1, templateGraph, startNode_1, endNode_1);
        assertEquals(testGraph_1, finalGraph_1, "Something is wrong transformedGraph: \n" + finalGraph_1.toString() + "\ntestGraph: \n" + testGraph_1.toString());

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph_2 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node_2_0, instanceId_2, finalGraph_2, templateGraph, startNode_2, endNode_2);
        assertEquals(testGraph_2, finalGraph_2, "Something is wrong transformedGraph: \n" + finalGraph_2.toString() + "\ntestGraph: \n" + testGraph_2.toString());

        checkIfTheRightAmountOfNodesAreInBothGraphs(2, finalGraph_1, finalGraph_2);

        Graph<INode, DefaultEdge> combinedGraph = (Graph<INode, DefaultEdge>) ((AbstractBaseGraph)finalGraph_1).clone();
        Graphs.addGraph(combinedGraph, finalGraph_1);
        Graphs.addGraph(combinedGraph, finalGraph_2);

        checkSuccessorOrPredecessorAmount(0, combinedGraph, Graphs::successorListOf, endNode_1, endNode_2);
        checkSuccessorOrPredecessorAmount(1, combinedGraph, Graphs::successorListOf, node_1_0, node_1_4b, node_1_3b, node_1_5, node_2_0, node_2_4b, node_2_3b, node_2_5, node_1_6, node_2_6);
        checkSuccessorOrPredecessorAmount(2, combinedGraph, Graphs::successorListOf, node_1_2, node_1_1, node_2_2, node_2_1, node4a, node3a);

        checkSuccessorOrPredecessorAmount(0, combinedGraph, Graphs::predecessorListOf, startNode_1, startNode_2);
        checkSuccessorOrPredecessorAmount(1, combinedGraph, Graphs::predecessorListOf, node_1_0, node_2_0, node_1_1, node_1_4b, node_1_3b, node_2_1, node_2_4b, node_2_3b);
        checkSuccessorOrPredecessorAmount(2, combinedGraph, Graphs::predecessorListOf, node_1_2, node4a, node3a, node_1_5, node_2_2, node_2_5);
    }

    @Test
    void transformation_sequentialTemplate_3() {

        templateGraph.addEdge(templateNode0, templateNode1);
        templateGraph.addEdge(templateNode1, templateNode2);
        templateGraph.addEdge(templateNode2, templateNode4);
        templateGraph.addEdge(templateNode4, templateNode3);
        templateGraph.addEdge(templateNode3, templateNode5);
        templateGraph.addEdge(templateNode5, templateNode6);

        testGraph_1.addEdge(startNode_1, node_1_0);
        testGraph_1.addEdge(node_1_0, node_1_1);
        testGraph_1.addEdge(node_1_1, node_1_2);
        testGraph_1.addEdge(node_1_2, node4a);
        testGraph_1.addEdge(node_1_2, node_1_4b);
        testGraph_1.addEdge(node4a, node3a);
        testGraph_1.addEdge(node_1_4b, node_1_3b);
        testGraph_1.addEdge(node3a, node_1_5);
        testGraph_1.addEdge(node_1_3b, node_1_5);
        testGraph_1.addEdge(node_1_5, node_1_6);
        testGraph_1.addEdge(node_1_6, endNode_1);

        testGraph_2.addEdge(startNode_2, node_2_0);
        testGraph_2.addEdge(node_2_0, node_2_1);
        testGraph_2.addEdge(node_2_1, node_2_2);
        testGraph_2.addEdge(node_2_2, node4a);
        testGraph_2.addEdge(node_2_2, node_2_4b);
        testGraph_2.addEdge(node4a, node3a);
        testGraph_2.addEdge(node_2_4b, node_2_3b);
        testGraph_2.addEdge(node3a, node_2_5);
        testGraph_2.addEdge(node_2_3b, node_2_5);
        testGraph_2.addEdge(node_2_5, node_2_6);
        testGraph_2.addEdge(node_2_6, endNode_2);

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph_1 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node_1_0, instanceId_1, finalGraph_1, templateGraph, startNode_1, endNode_1);
        assertEquals(testGraph_1, finalGraph_1, "Something is wrong transformedGraph: \n" + finalGraph_1.toString() + "\ntestGraph: \n" + testGraph_1.toString());

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph_2 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node_2_0, instanceId_2, finalGraph_2, templateGraph, startNode_2, endNode_2);
        assertEquals(testGraph_2, finalGraph_2, "Something is wrong transformedGraph: \n" + finalGraph_2.toString() + "\ntestGraph: \n" + testGraph_2.toString());

        checkIfTheRightAmountOfNodesAreInBothGraphs(2, finalGraph_1, finalGraph_2);

        Graph<INode, DefaultEdge> combinedGraph = (Graph<INode, DefaultEdge>) ((AbstractBaseGraph)finalGraph_1).clone();
        Graphs.addGraph(combinedGraph, finalGraph_1);
        Graphs.addGraph(combinedGraph, finalGraph_2);

        checkSuccessorOrPredecessorAmount(0, combinedGraph, Graphs::successorListOf, endNode_1, endNode_2);
        checkSuccessorOrPredecessorAmount(1, combinedGraph, Graphs::successorListOf, node4a, node_1_0, node_1_1, node_1_4b, node_1_3b, node_1_5, node_2_0, node_2_1, node_2_4b, node_2_3b, node_2_5, node_1_6, node_2_6);
        checkSuccessorOrPredecessorAmount(2, combinedGraph, Graphs::successorListOf, node3a, node_1_2, node_2_2);

        checkSuccessorOrPredecessorAmount(0, combinedGraph, Graphs::predecessorListOf, startNode_1, startNode_2);
        checkSuccessorOrPredecessorAmount(1, combinedGraph, Graphs::predecessorListOf, node_1_0, node_2_0, node3a, node_1_1, node_1_2, node_1_4b, node_1_3b, node_2_1, node_2_2, node_2_4b, node_2_3b);
        checkSuccessorOrPredecessorAmount(2, combinedGraph, Graphs::predecessorListOf, node4a, node_1_5, node_2_5);
    }


    @Test
    void transformation_parallelTemplate_1() {

        templateGraph.addEdge(templateNode0, templateNode1);
        templateGraph.addEdge(templateNode0, templateNode2);
        templateGraph.addEdge(templateNode1, templateNode3);
        templateGraph.addEdge(templateNode2, templateNode4);
        templateGraph.addEdge(templateNode3, templateNode5);
        templateGraph.addEdge(templateNode4, templateNode5);
        templateGraph.addEdge(templateNode5, templateNode6);

        testGraph_1.addEdge(startNode_1, node_1_0);
        testGraph_1.addEdge(node_1_0,  node_1_1);
        testGraph_1.addEdge(node_1_0,  node_1_2);
        testGraph_1.addEdge(node_1_1,  node3a);
        testGraph_1.addEdge(node_1_1,  node_1_3b);
        testGraph_1.addEdge(node_1_2,  node4a);
        testGraph_1.addEdge(node_1_2,  node_1_4b);
        testGraph_1.addEdge(node3a,    node_1_5);
        testGraph_1.addEdge(node_1_3b, node_1_5);
        testGraph_1.addEdge(node4a,    node_1_5);
        testGraph_1.addEdge(node_1_4b, node_1_5);
        testGraph_1.addEdge(node_1_5,  node_1_6);
        testGraph_1.addEdge(node_1_6, endNode_1);

        testGraph_2.addEdge(startNode_2, node_2_0);
        testGraph_2.addEdge(node_2_0,  node_2_1);
        testGraph_2.addEdge(node_2_0,  node_2_2);
        testGraph_2.addEdge(node_2_1,  node3a);
        testGraph_2.addEdge(node_2_1,  node_2_3b);
        testGraph_2.addEdge(node_2_2,  node4a);
        testGraph_2.addEdge(node_2_2,  node_2_4b);
        testGraph_2.addEdge(node3a,    node_2_5);
        testGraph_2.addEdge(node_2_3b, node_2_5);
        testGraph_2.addEdge(node4a,    node_2_5);
        testGraph_2.addEdge(node_2_4b, node_2_5);
        testGraph_2.addEdge(node_2_5,  node_2_6);
        testGraph_2.addEdge(node_2_6, endNode_2);

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph_1 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node_1_0, instanceId_1, finalGraph_1, templateGraph, startNode_1, endNode_1);
        assertEquals(testGraph_1, finalGraph_1, "Something is wrong transformedGraph: \n" + finalGraph_1.toString() + "\ntestGraph: \n" + testGraph_1.toString());

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph_2 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node_2_0, instanceId_2, finalGraph_2, templateGraph, startNode_2, endNode_2);
        assertEquals(testGraph_2, finalGraph_2, "Something is wrong transformedGraph: \n" + finalGraph_2.toString() + "\ntestGraph: \n" + testGraph_2.toString());

        checkIfTheRightAmountOfNodesAreInBothGraphs(2, finalGraph_1, finalGraph_2);

        Graph<INode, DefaultEdge> combinedGraph = (Graph<INode, DefaultEdge>) ((AbstractBaseGraph)finalGraph_1).clone();
        Graphs.addGraph(combinedGraph, finalGraph_1);
        Graphs.addGraph(combinedGraph, finalGraph_2);

        checkSuccessorOrPredecessorAmount(0, combinedGraph, Graphs::successorListOf, endNode_1, endNode_2);
        checkSuccessorOrPredecessorAmount(1, combinedGraph, Graphs::successorListOf, node_1_3b, node_1_4b, node_1_5, node_2_3b, node_2_4b, node_2_5, node_1_6, node_2_6);
        checkSuccessorOrPredecessorAmount(2, combinedGraph, Graphs::successorListOf, node3a, node4a, node_1_0, node_1_1, node_1_2, node_2_0, node_2_1, node_2_2);

        checkSuccessorOrPredecessorAmount(0, combinedGraph, Graphs::predecessorListOf, startNode_1, startNode_2);
        checkSuccessorOrPredecessorAmount(1, combinedGraph, Graphs::predecessorListOf, node_1_0, node_2_0, node_1_1, node_1_2, node_1_3b, node_1_4b, node_1_6, node_2_1, node_2_2, node_2_3b, node_2_4b, node_2_6);
        checkSuccessorOrPredecessorAmount(2, combinedGraph, Graphs::predecessorListOf, node3a, node4a);
        checkSuccessorOrPredecessorAmount(4, combinedGraph, Graphs::predecessorListOf, node_1_5, node_2_5);
    }

    @Test
    void transformation_parallelTemplate_2() {

        templateGraph.addEdge(templateNode0, templateNode1);
        templateGraph.addEdge(templateNode0, templateNode2);
        templateGraph.addEdge(templateNode1, templateNode5);
        templateGraph.addEdge(templateNode2, templateNode5);
        templateGraph.addEdge(templateNode5, templateNode3);
        templateGraph.addEdge(templateNode5, templateNode4);
        templateGraph.addEdge(templateNode3, templateNode6);
        templateGraph.addEdge(templateNode4, templateNode6);

        testGraph_1.addEdge(startNode_1, node_1_0);
        testGraph_1.addEdge(node_1_0,  node_1_1);
        testGraph_1.addEdge(node_1_0,  node_1_2);
        testGraph_1.addEdge(node_1_1,  node_1_5);
        testGraph_1.addEdge(node_1_2,  node_1_5);
        testGraph_1.addEdge(node_1_5,  node3a);
        testGraph_1.addEdge(node_1_5,  node_1_3b);
        testGraph_1.addEdge(node_1_5,  node4a);
        testGraph_1.addEdge(node_1_5,  node_1_4b);
        testGraph_1.addEdge(node3a,    node_1_6);
        testGraph_1.addEdge(node_1_3b, node_1_6);
        testGraph_1.addEdge(node4a,    node_1_6);
        testGraph_1.addEdge(node_1_4b, node_1_6);
        testGraph_1.addEdge(node_1_6, endNode_1);

        testGraph_2.addEdge(startNode_2, node_2_0);
        testGraph_2.addEdge(node_2_0,  node_2_1);
        testGraph_2.addEdge(node_2_0,  node_2_2);
        testGraph_2.addEdge(node_2_1,  node_2_5);
        testGraph_2.addEdge(node_2_2,  node_2_5);
        testGraph_2.addEdge(node_2_5,  node3a);
        testGraph_2.addEdge(node_2_5,  node_2_3b);
        testGraph_2.addEdge(node_2_5,  node4a);
        testGraph_2.addEdge(node_2_5,  node_2_4b);
        testGraph_2.addEdge(node3a,    node_2_6);
        testGraph_2.addEdge(node_2_3b, node_2_6);
        testGraph_2.addEdge(node4a,    node_2_6);
        testGraph_2.addEdge(node_2_4b, node_2_6);
        testGraph_2.addEdge(node_2_6, endNode_2);

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph_1 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node_1_0, instanceId_1, finalGraph_1, templateGraph, startNode_1, endNode_1);
        assertEquals(testGraph_1, finalGraph_1, "Something is wrong transformedGraph: \n" + finalGraph_1.toString() + "\ntestGraph: \n" + testGraph_1.toString());

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph_2 = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node_2_0, instanceId_2, finalGraph_2, templateGraph, startNode_2, endNode_2);
        assertEquals(testGraph_2, finalGraph_2, "Something is wrong transformedGraph: \n" + finalGraph_2.toString() + "\ntestGraph: \n" + testGraph_2.toString());

        checkIfTheRightAmountOfNodesAreInBothGraphs(2, finalGraph_1, finalGraph_2);

        Graph<INode, DefaultEdge> combinedGraph = (Graph<INode, DefaultEdge>) ((AbstractBaseGraph)finalGraph_1).clone();
        Graphs.addGraph(combinedGraph, finalGraph_1);
        Graphs.addGraph(combinedGraph, finalGraph_2);

        checkSuccessorOrPredecessorAmount(0, combinedGraph, Graphs::successorListOf, endNode_1, endNode_2);
        checkSuccessorOrPredecessorAmount(1, combinedGraph, Graphs::successorListOf, node_1_1, node_1_2, node_1_3b, node_1_4b, node_2_1, node_2_2, node_2_3b, node_2_4b, node_1_6, node_2_6);
        checkSuccessorOrPredecessorAmount(2, combinedGraph, Graphs::successorListOf, node3a, node4a, node_1_0, node_2_0);
        checkSuccessorOrPredecessorAmount(4, combinedGraph, Graphs::successorListOf, node_1_5, node_2_5);

        checkSuccessorOrPredecessorAmount(0, combinedGraph, Graphs::predecessorListOf, startNode_1, startNode_2);
        checkSuccessorOrPredecessorAmount(1, combinedGraph, Graphs::predecessorListOf, node_1_0, node_2_0, node_1_1, node_1_2, node_1_3b, node_1_4b, node_2_1, node_2_2, node_2_3b, node_2_4b);
        checkSuccessorOrPredecessorAmount(2, combinedGraph, Graphs::predecessorListOf, node3a, node4a, node_1_5, node_2_5);

    }

    private void checkIfTheRightAmountOfNodesAreInBothGraphs(int expectedAmount, Graph<INode, DefaultEdge> graph1, Graph<INode, DefaultEdge> graph2) {
        List<INode> intersect = graph1.vertexSet().stream().filter(graph2.vertexSet()::contains).collect(Collectors.toList());
        assertThat(intersect, hasSize(expectedAmount));
    }

    private void checkSuccessorOrPredecessorAmount(int expectedAmount, Graph<INode, DefaultEdge> graph, BiFunction<Graph<INode, DefaultEdge>, INode, List<INode>> function, INode... nodes) {
        for (INode node : nodes) {
            assertThat(node.getLabel(), function.apply(graph, node), hasSize(expectedAmount));
        }
    }

}