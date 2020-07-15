package at.ac.wuwien.causalminer.frontend.rest.instance;

import at.ac.wuwien.causalminer.frontend.model.paging.PagingRequest;
import at.ac.wuwien.causalminer.frontend.model.tables.ProcessViewTableEntry;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.CytoscapeElement;
import at.ac.wuwien.causalminer.frontend.model.paging.ViewPage;
import at.ac.wuwien.causalminer.frontend.rest.ViewDataUtility;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.services.ProcessInstanceServices;
import at.ac.wuwien.causalminer.neo4jdb.services.StartActivityServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/process/instance")
public class InstancesRestController {

    @Autowired
    private ProcessInstanceServices processInstanceServices;
    @Autowired
    private StartActivityServices startActivityServices;
    @Autowired
    private ViewDataUtility viewDataUtility;


    @GetMapping(value = "all")
    public CytoscapeElement getAllCases() {
        List<InstanceActivity> neo4jResult = processInstanceServices.findAllCases();
        return viewDataUtility.createStartEndFilteredViewGraph(neo4jResult);
    }

    @GetMapping(value = "{instanceId}")
    public CytoscapeElement getByInstanceId(@PathVariable("instanceId") String instanceId) {
        List<InstanceActivity> neo4jResult = processInstanceServices.findByInstanceId(instanceId);
        return viewDataUtility.createStartEndFilteredViewGraph(neo4jResult);
    }

    @PostMapping(value = "ofModel/{modelId}/table")
    public ViewPage<ProcessViewTableEntry> getInstancesByModelId(@PathVariable("modelId") String modelId, @RequestBody PagingRequest pagingRequest) {
        List<InstanceStartActivity> neo4jPage = startActivityServices.findByModelId(modelId);
        List<ProcessViewTableEntry> caseTableEntries = viewDataUtility.getInstanceTableEntries(neo4jPage.stream(), "OrderNr");
        ViewPage<ProcessViewTableEntry> viewPage = new ViewPage<>(caseTableEntries);
        viewPage.setDraw(pagingRequest.getDraw());
        return viewPage;
    }

    @PostMapping(value = "all/table")
    public ViewPage<ProcessViewTableEntry> list(@RequestBody PagingRequest pagingRequest) {
        List<InstanceStartActivity> neo4jPage = startActivityServices.findAll(pagingRequest.getStart(), pagingRequest.getLength());
        List<ProcessViewTableEntry> caseTableEntries = viewDataUtility.getInstanceTableEntries(neo4jPage.stream(), "OrderNr");
        ViewPage<ProcessViewTableEntry> viewPage = new ViewPage<>(caseTableEntries);
        viewPage.setDraw(pagingRequest.getDraw());
        return viewPage;
    }

}
