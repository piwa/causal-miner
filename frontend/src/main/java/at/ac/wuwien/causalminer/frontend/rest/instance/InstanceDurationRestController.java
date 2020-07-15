package at.ac.wuwien.causalminer.frontend.rest.instance;

import at.ac.wuwien.causalminer.frontend.model.paging.PagingRequest;
import at.ac.wuwien.causalminer.frontend.model.paging.ViewPage;
import at.ac.wuwien.causalminer.frontend.model.tables.DurationTableEntry;
import at.ac.wuwien.causalminer.frontend.rest.ViewDataUtility;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.TotalDurationsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.services.ProcessInstanceServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/process/instance/durations")
public class InstanceDurationRestController {

    @Autowired
    private ProcessInstanceServices processInstanceServices;
    @Autowired
    private ViewDataUtility viewDataUtility;

    
    @PostMapping(value = "table")
    public ViewPage<DurationTableEntry> durationsTable(@RequestBody PagingRequest pagingRequest) {
        List<TotalDurationsQueryResult> results = processInstanceServices.getTotalDurationsFromStartToEnd();

        List<DurationTableEntry> durationTableEntry = viewDataUtility.getDurationTableEntries(results.stream());
        ViewPage<DurationTableEntry> viewPage = new ViewPage<>(durationTableEntry);
        viewPage.setDraw(pagingRequest.getDraw());

        return viewPage;
    }





}
