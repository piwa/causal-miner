package at.ac.wuwien.causalminer.frontend.rest.instance;

import at.ac.wuwien.causalminer.frontend.model.paging.PagingRequest;
import at.ac.wuwien.causalminer.frontend.configuration.NodeStyleProperties;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.CytoscapeElement;
import at.ac.wuwien.causalminer.frontend.model.paging.ViewPage;
import at.ac.wuwien.causalminer.frontend.model.tables.ConnectedInstanceAmountTableEntry;
import at.ac.wuwien.causalminer.frontend.rest.ViewDataUtility;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.EventTypeResult;
import at.ac.wuwien.causalminer.neo4jdb.services.AggregateProcessInstanceServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/process/instance")
public class AggregatedInstancesRestController {

    @Autowired
    private AggregateProcessInstanceServices aggregateProcessInstanceServices;
    @Autowired
    private NodeStyleProperties nodeStyleProperties;
    @Autowired
    private ViewDataUtility viewDataUtility;

    @GetMapping(value = "eventType")
    public CytoscapeElement getEventTypeView(@RequestParam("eventType") String eventType, @RequestParam("origId") String origId) {
        List<InstanceActivity> neo4jResult = aggregateProcessInstanceServices.findByEvent(eventType, origId);
        return viewDataUtility.createStartEndFilteredViewGraph(neo4jResult);
    }

    @GetMapping(value = "aggregate")
    public CytoscapeElement getAggregateInstances(@RequestParam("eventType") String eventTypeLabel, @RequestParam("origId") String origId,
                                                  @RequestParam(value = "aggregateConfiguration", defaultValue = "aggregateGlobal", required = false) String aggregateConfiguration) {

        String eventType = eventTypeLabel;
        List<ModelActivity> neo4jResult;
        if (aggregateConfiguration.equals("aggregateGlobal")) {                          // TODO use enum
            neo4jResult = aggregateProcessInstanceServices.aggregateTotalByEvent(eventType, origId);
        } else {
            neo4jResult = aggregateProcessInstanceServices.findByEventAndAggregateNeighbors(eventType, origId);
        }

        return viewDataUtility.createStartEndFilteredViewGraph(neo4jResult);

    }

    @PostMapping(value = "aggregate/table")
    public ViewPage<ConnectedInstanceAmountTableEntry> getAggregateInstancesTable(@RequestParam("eventType") String eventTypeLabel, @RequestParam("origId") Optional<String> origId, @RequestBody PagingRequest pagingRequest) {

        Optional<Map.Entry<String, String>> eventTypeEntry = nodeStyleProperties.getLabel().entrySet().stream().filter(entry -> entry.getValue().equals(eventTypeLabel)).findAny();

        if (eventTypeEntry.isPresent()) {
            String eventType = eventTypeEntry.get().getKey().toUpperCase();
            List<EventTypeResult> neo4jPage;
            if (origId.isPresent()) {
                neo4jPage = aggregateProcessInstanceServices.getEventTypes(eventType, origId.get());
            } else {
                neo4jPage = aggregateProcessInstanceServices.getEventTypes(eventType);
            }

            List<ConnectedInstanceAmountTableEntry> caseTableEntries = viewDataUtility.getConnectedInstanceAmountTableEntries(neo4jPage.stream(), eventType);
            ViewPage<ConnectedInstanceAmountTableEntry> viewPage = new ViewPage<>(caseTableEntries);
            viewPage.setDraw(pagingRequest.getDraw());
            return viewPage;
        }
        ViewPage<ConnectedInstanceAmountTableEntry> viewPage = new ViewPage<>();
        viewPage.setDraw(pagingRequest.getDraw());
        return viewPage;
    }

}
