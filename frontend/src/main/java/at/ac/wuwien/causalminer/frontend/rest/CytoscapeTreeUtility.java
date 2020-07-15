package at.ac.wuwien.causalminer.frontend.rest;

import at.ac.wuwien.causalminer.frontend.model.cytoscape.CytoscapeEdge;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.CytoscapeElement;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.CytoscapeNode;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.CytoscapeNodeData;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.*;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;

@Slf4j
public class CytoscapeTreeUtility {

    private Map<String, String> labelsMap = new HashMap<>();
    private Map<String, String> reversedLabelsMap = new HashMap<>();

    public CytoscapeTreeUtility(Map<String, String> labelsMap) {
        this.labelsMap = labelsMap;
        labelsMap.forEach((k, v) -> reversedLabelsMap.put(v, k));
    }

    public List<List<String>> getLabelsOfAllPaths(CytoscapeElement cytoscapeElement) {

        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        Map<String, CytoscapeNodeData> nodes = new HashMap<>();
        cytoscapeElement.getNodes().stream().map(CytoscapeNode::getData).forEach(data -> {
            nodes.put(data.getId(), data);
            graph.addVertex(data.getId());
        });

        cytoscapeElement.getEdges().stream().map(CytoscapeEdge::getData).forEach(data -> {
            graph.addEdge(data.getSource(), data.getTarget());
        });

        Set<String> startNodes = new HashSet<>();
        Set<String> leaveNodes = new HashSet<>();
        Iterator<String> iter = new DepthFirstIterator<>(graph);
        while (iter.hasNext()) {
            String vertex = iter.next();
            if(graph.inDegreeOf(vertex) > 0 && graph.outDegreeOf(vertex) == 0) {
                leaveNodes.add(vertex);
            }
            if(graph.inDegreeOf(vertex) == 0 && graph.outDegreeOf(vertex) > 0) {
                startNodes.add(vertex);
            }
        }

        AllDirectedPaths<String, DefaultEdge> allDirectedPaths = new AllDirectedPaths<>(graph);
        List<GraphPath<String, DefaultEdge>> paths = allDirectedPaths.getAllPaths(startNodes, leaveNodes, false, 100);

        List<List<String>> labelPaths = new ArrayList<>();
        for (GraphPath<String, DefaultEdge> path : paths) {
            List<String> subLabelPath = new ArrayList<>();

            for (String node : path.getVertexList()) {
                if (startNodes.contains(node)) {
                    subLabelPath = new ArrayList<>();
                    labelPaths.add(subLabelPath);
                }

                String label = nodes.get(node).getLabel();
                subLabelPath.add(reversedLabelsMap.getOrDefault(label, label).toUpperCase());   // TODO get rid of toUpperCases
            }
        }

        return labelPaths;
    }

}
