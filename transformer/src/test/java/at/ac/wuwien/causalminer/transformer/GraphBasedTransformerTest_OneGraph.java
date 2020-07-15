package at.ac.wuwien.causalminer.transformer;

import at.ac.wuwien.causalminer.transformer.model.StartNode;
import at.ac.wuwien.causalminer.transformer.model.template.CardinalRelationship;
import at.ac.wuwien.causalminer.transformer.model.EndNode;
import at.ac.wuwien.causalminer.transformer.model.INode;
import at.ac.wuwien.causalminer.transformer.model.Node;
import at.ac.wuwien.causalminer.transformer.model.*;
import at.ac.wuwien.causalminer.transformer.model.template.TemplateNode;
import lombok.extern.slf4j.Slf4j;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = TestApplicationContext.class)
@ActiveProfiles("test")
@Import(TestConfiguration.class)
@Slf4j
class GraphBasedTransformerTest_OneGraph {

    private Node node0;
    private Node node1;
    private Node node2;
    private Node node3a;
    private Node node3b;
    private Node node4a;
    private Node node4b;
    private Node node5;
    private Node node6;

    private TemplateNode templateNode0;
    private TemplateNode templateNode1;
    private TemplateNode templateNode2;
    private TemplateNode templateNode3;
    private TemplateNode templateNode4;
    private TemplateNode templateNode5;
    private TemplateNode templateNode6;

    @Autowired
    private GraphBasedTransformer graphBasedTransformer;

    private StartNode startNode;
    private EndNode endNode;

    private List<INode> nodeList = new ArrayList<>();
    private List<TemplateNode> templateNodeList = new ArrayList<>();

    private DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph;
    private DefaultDirectedTestGraph<INode, CardinalRelationship> testGraph;

    private UUID instanceId;

    @BeforeEach
    public void TimeSetting1() {
        instanceId = UUID.randomUUID();
        startNode = StartAndEndNodeUtility.createStartNode(instanceId, new HashMap<>());
        endNode = StartAndEndNodeUtility.createEndNode(startNode.getInstanceId());

        DateTime startDateTime = new DateTime(0);
        DateTime endDateTime = (new DateTime(0)).plusHours(1);
        node0 = new Node("0", "0", startDateTime, endDateTime, null);
        node1 = new Node("1", "1", startDateTime, endDateTime, null);
        node2 = new Node("2", "2", startDateTime, endDateTime, null);
        node3a = new Node("3a", "3", startDateTime, endDateTime, null);
        node3b = new Node("3b", "3", startDateTime, endDateTime, null);
        node4a = new Node("4a", "4", startDateTime, endDateTime, null);
        node4b = new Node("4b", "4", startDateTime, endDateTime, null);
        node5 = new Node("5", "5", startDateTime, endDateTime, null);
        node6 = new Node("6", "6", startDateTime, endDateTime, null);
        nodeList.add(node0);
        nodeList.add(node1);
        nodeList.add(node2);
        nodeList.add(node3a);
        nodeList.add(node3b);
        nodeList.add(node4a);
        nodeList.add(node4b);
        nodeList.add(node5);
        nodeList.add(node6);
        nodeList.forEach(node -> node.getInstanceIds().add(instanceId));

        addNodeRelationship(node0, node1);
        addNodeRelationship(node1, node2);
        addNodeRelationship(node2, node3a);
        addNodeRelationship(node2, node3b);
        addNodeRelationship(node3a, node4a);
        addNodeRelationship(node3b, node4b);
        addNodeRelationship(node4a, node5);
        addNodeRelationship(node4b, node5);
        addNodeRelationship(node5, node6);

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

        testGraph = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        nodeList.forEach(testGraph::addVertex);
        testGraph.addVertex(startNode);
        testGraph.addVertex(endNode);

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

        testGraph.addEdge(startNode, node0);
        testGraph.addEdge(node0, node1);
        testGraph.addEdge(node1, node2);
        testGraph.addEdge(node2, node3a);
        testGraph.addEdge(node2, node3b);
        testGraph.addEdge(node3a, node4a);
        testGraph.addEdge(node3b, node4b);
        testGraph.addEdge(node4a, node5);
        testGraph.addEdge(node4b, node5);
        testGraph.addEdge(node5, node6);
        testGraph.addEdge(node6, endNode);

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node0, instanceId, finalGraph, templateGraph, startNode, endNode);

        assertEquals(testGraph, finalGraph, "Something is wrong transformedGraph: \n" + finalGraph.toString() + "\ntestGraph: \n" + testGraph.toString());
    }

    @Test
    void transformation_sequentialTemplate_2() {

        templateGraph.addEdge(templateNode0, templateNode1);
        templateGraph.addEdge(templateNode1, templateNode4);
        templateGraph.addEdge(templateNode4, templateNode2);
        templateGraph.addEdge(templateNode2, templateNode3);
        templateGraph.addEdge(templateNode3, templateNode5);
        templateGraph.addEdge(templateNode5, templateNode6);

        testGraph.addEdge(startNode, node0);
        testGraph.addEdge(node0, node1);
        testGraph.addEdge(node1, node4a);
        testGraph.addEdge(node1, node4b);
        testGraph.addEdge(node4a, node2);
        testGraph.addEdge(node4b, node2);
        testGraph.addEdge(node2, node3a);
        testGraph.addEdge(node2, node3b);
        testGraph.addEdge(node3a, node5);
        testGraph.addEdge(node3b, node5);
        testGraph.addEdge(node5, node6);
        testGraph.addEdge(node6, endNode);

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node0, instanceId, finalGraph, templateGraph, startNode, endNode);

        assertEquals(testGraph, finalGraph, "Something is wrong transformedGraph: \n" + finalGraph.toString() + "\ntestGraph: \n" + testGraph.toString());
    }

    @Test
    void transformation_sequentialTemplate_3() {

        templateGraph.addEdge(templateNode0, templateNode1);
        templateGraph.addEdge(templateNode1, templateNode2);
        templateGraph.addEdge(templateNode2, templateNode4);
        templateGraph.addEdge(templateNode4, templateNode3);
        templateGraph.addEdge(templateNode3, templateNode5);
        templateGraph.addEdge(templateNode5, templateNode6);

        testGraph.addEdge(startNode, node0);
        testGraph.addEdge(node0, node1);
        testGraph.addEdge(node1, node2);
        testGraph.addEdge(node2, node4a);
        testGraph.addEdge(node2, node4b);
        testGraph.addEdge(node4a, node3a);
        testGraph.addEdge(node4b, node3b);
        testGraph.addEdge(node3a, node5);
        testGraph.addEdge(node3b, node5);
        testGraph.addEdge(node5, node6);
        testGraph.addEdge(node6, endNode);

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node0, instanceId, finalGraph, templateGraph, startNode, endNode);

        assertEquals(testGraph, finalGraph, "Something is wrong transformedGraph: \n" + finalGraph.toString() + "\ntestGraph: \n" + testGraph.toString());
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

        testGraph.addEdge(startNode, node0);
        testGraph.addEdge(node0, node1);
        testGraph.addEdge(node0, node2);
        testGraph.addEdge(node1, node3a);
        testGraph.addEdge(node1, node3b);
        testGraph.addEdge(node2, node4a);
        testGraph.addEdge(node2, node4b);
        testGraph.addEdge(node3a, node5);
        testGraph.addEdge(node3b, node5);
        testGraph.addEdge(node4a, node5);
        testGraph.addEdge(node4b, node5);
        testGraph.addEdge(node5, node6);
        testGraph.addEdge(node6, endNode);

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node0, instanceId, finalGraph, templateGraph, startNode, endNode);

        assertEquals(testGraph, finalGraph, "Something is wrong transformedGraph: \n" + finalGraph.toString() + "\ntestGraph: \n" + testGraph.toString());
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

        testGraph.addEdge(startNode, node0);
        testGraph.addEdge(node0, node1);
        testGraph.addEdge(node0, node2);
        testGraph.addEdge(node1, node5);
        testGraph.addEdge(node2, node5);
        testGraph.addEdge(node5, node3a);
        testGraph.addEdge(node5, node3b);
        testGraph.addEdge(node5, node4a);
        testGraph.addEdge(node5, node4b);
        testGraph.addEdge(node3a, node6);
        testGraph.addEdge(node3b, node6);
        testGraph.addEdge(node4a, node6);
        testGraph.addEdge(node4b, node6);
        testGraph.addEdge(node6, endNode);

        DefaultDirectedImporterGraph<INode, DefaultEdge> finalGraph = new DefaultDirectedTestGraph<>(DefaultEdge.class);
        graphBasedTransformer.transform(node0, instanceId, finalGraph, templateGraph, startNode, endNode);

        assertEquals(testGraph, finalGraph, "Something is wrong transformedGraph: \n" + finalGraph.toString() + "\ntestGraph: \n" + testGraph.toString());
    }
}