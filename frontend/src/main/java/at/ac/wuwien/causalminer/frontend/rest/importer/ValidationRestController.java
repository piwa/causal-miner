package at.ac.wuwien.causalminer.frontend.rest.importer;

import at.ac.wuwien.causalminer.frontend.configuration.NodeStyleProperties;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.*;
import at.ac.wuwien.causalminer.frontend.model.paging.PagingRequest;
import at.ac.wuwien.causalminer.frontend.model.paging.ViewPage;
import at.ac.wuwien.causalminer.frontend.model.tables.ProcessInstanceViolationsTableEntry;
import at.ac.wuwien.causalminer.frontend.model.tables.ProcessViewTableEntry;
import at.ac.wuwien.causalminer.frontend.rest.ViewDataUtility;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.*;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.services.ProcessInstanceServices;
import at.ac.wuwien.causalminer.neo4jdb.services.StartActivityServices;
import at.ac.wuwien.causalminer.erp2graphdb.model.TransferredProcessInstance;
import at.ac.wuwien.causalminer.erp2graphdb.services.TransferredProcessInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/import/instance/validation")
public class ValidationRestController {

    @Autowired
    private TransferredProcessInstanceService transferredProcessInstanceService;
    @Autowired
    private ProcessInstanceServices processInstanceServices;
    @Autowired
    private StartActivityServices startActivityServices;
    @Autowired
    private ViewDataUtility viewDataUtility;
    @Autowired
    private NodeStyleProperties nodeStyleProperties;

    @PostMapping(value = "violations/all/table")
    public ViewPage<ProcessViewTableEntry> getAllViolatingProcessInstances(@RequestBody PagingRequest pagingRequest) {
        Iterable<TransferredProcessInstance> violatingImportedInstances = transferredProcessInstanceService.findAll(pagingRequest.getStart(), pagingRequest.getLength());

        Set<String> instanceIds = new HashSet<>();
        violatingImportedInstances.forEach(instance -> instanceIds.add(instance.getInstanceId().toString()));
        List<InstanceStartActivity> startActivities = startActivityServices.findAllByInstanceId(instanceIds);

        List<ProcessViewTableEntry> caseTableEntries = viewDataUtility.getInstanceTableEntries(startActivities.stream(), "OrderNr");
        ViewPage<ProcessViewTableEntry> viewPage = new ViewPage<>(caseTableEntries);
        viewPage.setDraw(pagingRequest.getDraw());
        return viewPage;
    }

    @PostMapping(value = "violations/{instanceId}/table")
    public ViewPage<ProcessInstanceViolationsTableEntry> getViolationsForProcessInstance(@PathVariable("instanceId") String instanceId, @RequestBody PagingRequest pagingRequest) {
        Optional<TransferredProcessInstance> violatingImportedInstances = transferredProcessInstanceService.findTransferredProcessInstanceByInstanceId(UUID.fromString(instanceId));

        ViewPage<ProcessInstanceViolationsTableEntry> viewPage = new ViewPage<>();
        violatingImportedInstances.ifPresent(transferredInstance -> {
            List<ProcessInstanceViolationsTableEntry> caseTableEntries = viewDataUtility.getProcessInstanceViolationsTableEntries(transferredInstance.getViolations());
            viewPage.setData(caseTableEntries);
        });
        viewPage.setDraw(pagingRequest.getDraw());
        return viewPage;
    }

    @GetMapping(value = "violations/{instanceId}/graph")
    public CytoscapeElement getByInstanceId(@PathVariable("instanceId") String instanceId) {
        List<InstanceActivity> neo4jResult = processInstanceServices.findByInstanceId(instanceId);
        Optional<TransferredProcessInstance> violatingImportedInstances = transferredProcessInstanceService.findTransferredProcessInstanceByInstanceId(UUID.fromString(instanceId));
        ArrayList<String> nodesWithViolations = new ArrayList<>();

        violatingImportedInstances.ifPresent(instance -> instance.getViolations().forEach(violation -> {
            nodesWithViolations.add(String.valueOf(violation.getViolationStartNode().getNodeGraphDbId()));
            if(violation.getViolationEndNode() != null) {
                nodesWithViolations.add(String.valueOf(violation.getViolationEndNode().getNodeGraphDbId()));
            }
        }));
        return viewDataUtility.createStartEndFilteredViewGraph(neo4jResult, nodesWithViolations);
    }

    @GetMapping(value = "loadTemplate")
    public CytoscapeElement loadTemplate() {

        return cytoscapeElement;
    }


    private CytoscapeNodeData getCytoscapeNodeData(String id, String label, Integer quantity) {
        CytoscapeNodeData nodeData = new CytoscapeNodeData(id, nodeStyleProperties.getLabel().getOrDefault(label.toLowerCase(), label) + "\n(" + quantity + ")", label);
        nodeData.setColor(nodeStyleProperties.getColor().getOrDefault(label.toLowerCase(), nodeStyleProperties.getDefaultColor()));
        return nodeData;
    }


}
