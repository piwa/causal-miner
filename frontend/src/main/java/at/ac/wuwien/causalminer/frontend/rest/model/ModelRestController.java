package at.ac.wuwien.causalminer.frontend.rest.model;

import at.ac.wuwien.causalminer.frontend.model.paging.PagingRequest;
import at.ac.wuwien.causalminer.frontend.model.tables.ProcessViewTableEntry;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.CytoscapeElement;
import at.ac.wuwien.causalminer.frontend.model.paging.ViewPage;
import at.ac.wuwien.causalminer.frontend.rest.ViewDataUtility;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.graphfunctions.GroupedGraphUtilities;
import at.ac.wuwien.causalminer.neo4jdb.services.ProcessModelServices;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/process/model")
public class ModelRestController {

    @Autowired
    private ProcessModelServices processModelServices;
    @Autowired
    private GroupedGraphUtilities groupedGraphUtilities;
    @Autowired
    private ViewDataUtility viewDataUtility;

    @GetMapping(value = "global")
    public CytoscapeElement getGlobalProcessModel() {
        List<ModelActivity> neo4jResult = processModelServices.findGlobalProcessModel();
        return viewDataUtility.createStartEndFilteredViewGraph(neo4jResult);
    }

    @GetMapping(value = "{modelId}")
    public CytoscapeElement getByInstanceId(@PathVariable("modelId") String modelId) {
        List<ModelActivity> neo4jResult = processModelServices.findByModelId(modelId);
        viewDataUtility.removeStartAndEndNode(neo4jResult);
        return viewDataUtility.createStartEndFilteredViewGraph(neo4jResult);
    }

    @PostMapping(value = "individual/table")
    public ViewPage<ProcessViewTableEntry> getIndividualProcessModelsTable(@RequestBody PagingRequest pagingRequest) {
        List<ModelStartActivity> neo4jResult = processModelServices.findAllIndividualProcessModels();

        Set<String> modelIds = neo4jResult.stream().map(ModelStartActivity::getModelId).collect(Collectors.toSet());
        List<ProcessViewTableEntry> tableEntries = viewDataUtility.getProcessViewTableEntries(modelIds);
        ViewPage<ProcessViewTableEntry> viewPage = new ViewPage<>(tableEntries);
        viewPage.setDraw(pagingRequest.getDraw());

        return viewPage;
    }

    @PostMapping(value = "combined/table")
    public ViewPage<ProcessViewTableEntry> getCombinedProcessModelsTable(@RequestBody PagingRequest pagingRequest) {
        List<ModelStartActivity> neo4jResult = processModelServices.findAllCombinedProcessModels();

        Set<String> modelIds = neo4jResult.stream().map(ModelStartActivity::getModelId).collect(Collectors.toSet());
        List<ProcessViewTableEntry> tableEntries = viewDataUtility.getProcessViewTableEntries(modelIds);
        ViewPage<ProcessViewTableEntry> viewPage = new ViewPage<>(tableEntries);
        viewPage.setDraw(pagingRequest.getDraw());

        return viewPage;
    }

    @GetMapping(value = "compare")
    public CytoscapeElement getProcessModelOfATimeFrame(@RequestParam Long startTime, @RequestParam Long endTime) {
        List<ModelActivity> neo4jResult = groupedGraphUtilities.findProcessModelOfTimeFrame(new DateTime(startTime), new DateTime(endTime));
        neo4jResult.removeIf(modelActivity -> !modelActivity.isEssentialForProcessDepiction());
        return viewDataUtility.createStartEndFilteredViewGraph(neo4jResult);
    }

    @GetMapping(value = "aggregate")
    public CytoscapeElement getAggregateModels(@RequestParam("eventType") String eventType, @RequestParam("origId") String origId) {
        List<ModelActivity> neo4jResult = processModelServices.createAggregatedProcessModel(eventType, origId);
        neo4jResult.forEach(groupedGraphUtilities::calculateRelationshipProbabilities);
        return viewDataUtility.createStartEndFilteredViewGraph(neo4jResult);
    }

}
