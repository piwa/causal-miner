package at.ac.wuwien.causalminer.erpimport;

import at.ac.wuwien.causalminer.erpimport.model.OrderDto;
import at.ac.wuwien.causalminer.transformer.*;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.CombinableIndividualModelsResult;
import at.ac.wuwien.causalminer.neo4jdb.graphfunctions.GroupedGraphUtilities;
import at.ac.wuwien.causalminer.neo4jdb.services.ProcessInstanceServices;
import at.ac.wuwien.causalminer.neo4jdb.services.ProcessModelServices;
import at.ac.wuwien.causalminer.transformer.model.EndNode;
import at.ac.wuwien.causalminer.transformer.model.INode;
import at.ac.wuwien.causalminer.transformer.model.StartNode;
import at.ac.wuwien.causalminer.transformer.model.template.CardinalRelationship;
import at.ac.wuwien.causalminer.transformer.model.template.TemplateNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Transformer implements CommandLineRunner {

    @Autowired
    private ProcessInstanceServices processInstanceServices;
    @Autowired
    private ProcessModelServices processModelServices;
    @Autowired
    private FakeData fakeData;
    @Autowired
    private GroupedGraphUtilities groupedGraphUtilities;
    @Autowired
    private ReadErpDataForGraphBased readErpDataForGraphBased;
    @Autowired
    private GraphBasedTransformer graphBasedTransformer;
    @Autowired
    private GraphToProcessInstanceConverter graphToProcessInstanceConverter;
    @Autowired
    private GraphDbTemplateViolationChecker graphDbTemplateViolationChecker;

    @Value("${erp2graph.transformation.withChildInformation}")
    private boolean withChildInformation;

    @Value("${erp2graph.transformation.fake.withClonedNodes}")
    private boolean withAdditionalClonedNodes;
    @Value("${erp2graph.transformation.fake.clonedCasesAmount:1000}")
    private int clonedNodesAmount;
    @Value("${erp2graph.transformation.fake.withFakeTimes}")
    private boolean withFakeTimes;
    @Value("${erp2graph.transformation.saveAll.batch.size}")
    private int saveAllbatchSize;
    @Value("${erp2graph.transformation.printGraphToConsole}")
    private boolean printGraph;
    @Value("${erp2graph.transformation.createProcessModels}")
    private boolean createProcessModels;
    @Value("${erp2graph.transformation.createDiscoExport}")
    private boolean createDiscoExport;
    @Value("${erp2graph.transformation.createDiscoExport.csvOutputFile}")
    private String csvOutputFile;

    private DateTimeFormatter dateTimeOutput = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private String[] csvHeaders = {"CaseID", "Creation Timestamp", "Activity"};

    private DateTime startTimeOfTransformation = DateTime.now();

    private DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> templateGraph = new DefaultDirectedTemplateGraph<>(CardinalRelationship.class);
    private List<Graph<INode, DefaultEdge>> finalGraphs = new ArrayList<>();

    @Override
    @Transactional("erpTransactionManager")         // TODO is this ok at this position?
    public void run(String... args) throws Exception {

        startTimeOfTransformation = DateTime.now();

        templateGraph = getTemplateGraph();

        graphBasedTransformer.resetProperties();

        log.info("Reset Neo4j Database");
        processInstanceServices.deleteAll();
        processModelServices.deleteAll();

        log.info("Read erp data");
        List<OrderDto> fullOrderDtos = readErpDataForGraphBased.readErpDatabase();

        List<InstanceActivity> instanceActivityNodes = transformToGraphData(fullOrderDtos);

        if(createDiscoExport) {
            writeToCsvFile(fullOrderDtos);
        }

        StopWatch stopWatch = new StopWatch();
        persistToNeo4j(instanceActivityNodes, stopWatch);

        List<ModelActivity> groupedNodes = new ArrayList<>();
        if (createProcessModels) {
            groupedNodes = createGlobalProcessModel(stopWatch);
            Set<InstanceStartActivity> startActivities = instanceActivityNodes.stream().filter(activityNode -> activityNode.getType().equals("Start")).map(activity -> ((InstanceStartActivity) activity)).collect(Collectors.toSet());
            createIndividualProcessModel(startActivities, stopWatch);
//            createCombinedProcessModels(startActivities, stopWatch);
        }

        graphDbTemplateViolationChecker.checkForViolations(templateGraph);

        long caseAmount = instanceActivityNodes.stream().filter(node -> node.getType().equals("V_Order")).count();
        long relationshipAmount = instanceActivityNodes.stream().mapToInt(node -> node.getFollowUpRelationships().size()).sum();

        log.info("Summary: ");
        log.info("  --- Global Graph --- ");
        log.info("  Cases Amount: " + caseAmount);
        log.info("  Nodes Amount: " + instanceActivityNodes.size());
        log.info("  Relationships Amount: " + relationshipAmount);
        log.info("  Duration (sec): " + stopWatch.getTaskInfo()[0].getTimeSeconds());

        if (createProcessModels) {
            long groupedNodesCount = groupedNodes.stream().mapToLong(ModelActivity::getCount).sum();
            long groupedRelationshipAmount = groupedNodes.stream().mapToInt(node -> node.getFollowUpRelationships().size()).sum();
            long groupedRelationshipCount = groupedNodes.stream().mapToLong(node -> node.getFollowUpRelationships().stream().mapToLong(ModelRelationship::getCount).sum()).sum();
            log.info("  --- Grouped Graph --- ");
            log.info("  Nodes Amount: " + groupedNodes.size());
            log.info("  Relationships Amount: " + groupedRelationshipAmount);
            log.info("  Total Grouped Nodes Count: " + groupedNodesCount);
            log.info("  Total Grouped Relationships Count: " + groupedRelationshipCount);
            log.info("  Duration (sec): " + stopWatch.getTaskInfo()[1].getTimeSeconds());
        }

        log.info("Done");
        System.exit(0);
    }

    private DefaultDirectedTemplateGraph<TemplateNode, CardinalRelationship> getTemplateGraph() {


        // Return template


        return templateGraph;
    }


    private List<InstanceActivity> transformToGraphData(List<OrderDto> fullOrderDtos) {
        log.info("Transform erp to graph data");
        graphToProcessInstanceConverter.reset();

        fullOrderDtos.forEach(orderDto -> {
            try {
                log.info("  Pre-steps start");
                UUID instanceId = UUID.randomUUID();
                INode orderNode = orderDto.getNode(null, instanceId);
                log.info("  Pre-steps done");
                log.info("     Transform " + orderDto.getId());
                StartNode startNode = StartAndEndNodeUtility.createStartNode(instanceId, orderDto.getId().getEventIds());
                EndNode endNode = StartAndEndNodeUtility.createEndNode(startNode.getInstanceId());
                Graph<INode, DefaultEdge> finalGraph = graphBasedTransformer.transform(orderNode, startNode.getInstanceId(), templateGraph, startNode, endNode);
                renderGraph(finalGraph);

                graphToProcessInstanceConverter.createAndAddEvents(finalGraph);

                log.info("     Transformation done " + startNode.toString());
            } catch (Exception ex) {
                log.error("     Transformation Exception. Message=" + ex.getMessage(), ex);
            }
        });

        log.info("  Transformation done");
        log.info("  Combine graphs");

        List<InstanceActivity> resultingActivities = graphToProcessInstanceConverter.getInstanceActivities();
        graphToProcessInstanceConverter.reset();
        return resultingActivities;
    }

    private void renderGraph(Graph<INode, DefaultEdge> graph) {
        if (printGraph) {
            log.debug(graph.toString());
        }
    }

    private void persistToNeo4j(List<InstanceActivity> instanceActivityNodes, StopWatch stopWatch) {
        log.info("Persist transformed data to Neo4j");
        stopWatch.start("saveAll");

        int counter = 0;
        while (counter <= instanceActivityNodes.size()) {

            int endIndex = counter + saveAllbatchSize;
            if(endIndex > instanceActivityNodes.size()) {
                endIndex = instanceActivityNodes.size();
            }
            log.info("  Save from " + counter + " to " + endIndex + " instances to Neo4j");
            List<InstanceActivity> nodes = instanceActivityNodes.subList(counter, endIndex);
            log.info("      Save " + nodes.size() + " nodes to to Neo4j");
            processInstanceServices.saveAll(nodes);
            log.info("  Save from " + counter + " to " + counter + saveAllbatchSize + " instances to Neo4j done");
            counter = counter + saveAllbatchSize;
        }

        stopWatch.stop();
        log.info("Persist transformed data to Neo4j done");
    }

    private void createCombinedProcessModels(Set<InstanceStartActivity> startActivities, StopWatch stopWatch) {
        log.info("Create Combined Process Models");
        stopWatch.start("createCombinedProcessModels");
        Map<String, InstanceStartActivity> tempMap = new HashMap<>();
        startActivities.stream().map(InstanceStartActivity::getInstanceOfRelationships).flatMap(Collection::stream).forEach(ofRelationship -> {
            InstanceStartActivity instanceStartActivity = (InstanceStartActivity) ofRelationship.getStartEvent();
            ModelStartActivity modelStartActivity = (ModelStartActivity) ofRelationship.getEndEvent();
            tempMap.put(modelStartActivity.getModelId(), instanceStartActivity);
        });
        List<CombinableIndividualModelsResult> sameModelResults = processModelServices.findAllCasesWithSameProcessModel();
        sameModelResults.forEach(result -> {
            Set<InstanceStartActivity> startActivitySet = result.getSimilarIndividualModels().stream().map(tempMap::get).collect(Collectors.toSet());
            groupedGraphUtilities.createAndSaveCombinedProcessModel(startActivitySet);
        });
        stopWatch.stop();
        log.info("Create Combined Process Models done");
    }

    private List<ModelActivity> createGlobalProcessModel(StopWatch stopWatch) {
        log.info("Add Global Process Model");
        stopWatch.start("createGlobalProcessModel");
        List<ModelActivity> groupedNodes = groupedGraphUtilities.createAndSaveGlobalGroupedGraph();
        stopWatch.stop();
        log.info("Add Global Process Model done");
        return groupedNodes;
    }

    private void createIndividualProcessModel(Set<InstanceStartActivity> startActivities, StopWatch stopWatch) {
        log.info("Add Individual Process Models");
        stopWatch.start("createIndividualProcessModel");
        startActivities.forEach(activity -> groupedGraphUtilities.createAndSaveIndividualProcessModel(activity));
        stopWatch.stop();
        log.info("Add Individual Process Models done");
    }

    private void writeToCsvFile(List<OrderDto> fullOrderDtos) throws IOException {
        log.info("Export to CSV File");
        FileWriter out = new FileWriter(csvOutputFile);
        try (CSVPrinter printer = new CSVPrinter(out, org.apache.commons.csv.CSVFormat.DEFAULT.withHeader(csvHeaders))) {
            fullOrderDtos.forEach(orderDto -> {
                try {
                    UUID instanceId = UUID.randomUUID();
                    INode orderNode = orderDto.getNode(null, instanceId);

                    List<CsvOutputFormat> csvOutputs = new ArrayList<>();
                    getEventRecursively(csvOutputs, instanceId, orderNode);

                    for (CsvOutputFormat record : csvOutputs) {
                        printer.printRecord(record.getInstanceId(), record.getCreationTime(), record.getEventType());
                    }

                } catch (Exception ex) {
                    log.error("Transformation Exception. Message=" + ex.getMessage());
                }
            });
        }
        log.info("Export to CSV File Done");
    }

    private void getEventRecursively(List<CsvOutputFormat> csvOutputs, UUID instanceId, INode node) throws Exception {

        String creationTime = node.getActivityCreationTime().toString(dateTimeOutput);

        csvOutputs.add(new CsvOutputFormat(instanceId.toString(), creationTime, node.getEventType()));
        for (INode childNode : node.getChildren()) {
            getEventRecursively(csvOutputs, instanceId, childNode);
        }
    }

    @Data
    private class CsvOutputFormat {
        private final String instanceId;
        private final String creationTime;
        private final String eventType;
    }
}
