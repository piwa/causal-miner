package at.ac.wuwien.causalminer.frontend.rest.instance;

import at.ac.wuwien.causalminer.frontend.model.frappeGantt.FrappeGanttEntry;
import at.ac.wuwien.causalminer.frontend.model.tables.ProcessViewTableEntry;
import at.ac.wuwien.causalminer.frontend.model.jsgantt.GanttEntry;
import at.ac.wuwien.causalminer.frontend.model.paging.PagingRequest;
import at.ac.wuwien.causalminer.frontend.model.paging.ViewPage;
import at.ac.wuwien.causalminer.frontend.rest.ViewDataUtility;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceIntermediateActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.services.ProcessInstanceServices;
import at.ac.wuwien.causalminer.neo4jdb.services.StartActivityServices;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/process/instance/gantt")
public class InstanceGanttRestController {

    @Autowired
    private ProcessInstanceServices processInstanceServices;
    @Autowired
    private ViewDataUtility viewDataUtility;
    @Autowired
    private StartActivityServices startActivityServices;

    @PostMapping(value = "all/table")
    public ViewPage<ProcessViewTableEntry> list(@RequestBody PagingRequest pagingRequest) {
        List<InstanceStartActivity> neo4jPage = startActivityServices.findAll();
        List<ProcessViewTableEntry> caseTableEntries = viewDataUtility.getInstanceTableEntries(neo4jPage.stream(), "OrderNr");
        ViewPage<ProcessViewTableEntry> viewPage = new ViewPage<>(caseTableEntries);
        viewPage.setDraw(pagingRequest.getDraw());
        return viewPage;
    }

    @GetMapping(value = "/frappe/{instanceId}")
    public List<FrappeGanttEntry> getByInstanceIdFrappe(@PathVariable("instanceId") String instanceId) {
        List<InstanceActivity> neo4jResult = processInstanceServices.findByInstanceId(instanceId);

//        viewDataUtility.removeStartAndEndNode(neo4jResult);

        List<FrappeGanttEntry> ganttEntries = new ArrayList<>();

        Map<InstanceActivity, List<InstanceActivity>> childParentRelationships = new HashMap<>();

        neo4jResult.stream().map(InstanceActivity::getFollowUpRelationships).flatMap(List::stream)
                .forEach(rel ->
                        childParentRelationships.computeIfAbsent(rel.getEndEvent(), k -> new ArrayList<>()).add(rel.getStartEvent())
                );

        neo4jResult.forEach(activity -> {

            if (activity instanceof InstanceIntermediateActivity) {
                DateTime startTime = ((InstanceIntermediateActivity) activity).getCreationTimeOrNull();
                DateTime endTime = ((InstanceIntermediateActivity) activity).getChangeTimeOrNull();

                if (startTime != null && endTime != null) {
                    DateTimeFormatter dateTimeOutput = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                    String startTimeString = startTime.toString(dateTimeOutput);
                    String endTimeString = endTime.toString(dateTimeOutput);

                    FrappeGanttEntry ganttEntry = new FrappeGanttEntry();
                    ganttEntry.setId(String.valueOf(activity.getId()));
                    ganttEntry.setName(activity.getType());
                    ganttEntry.setStartDate(startTimeString);
                    ganttEntry.setEndDate(endTimeString);

                    String dependsOn = childParentRelationships.get(activity).stream()
                            .map(act -> String.valueOf(act.getId())).collect(Collectors.joining(","));
                    ganttEntry.setDependencies(dependsOn);

                    ganttEntries.add(ganttEntry);
                }
            }
        });

        return ganttEntries;
    }

    @GetMapping(value = "jgantt/{instanceId}")
    public List<GanttEntry> getByInstanceId_jgantt(@PathVariable("instanceId") String instanceId) {
        List<InstanceActivity> neo4jResult = processInstanceServices.findByInstanceId(instanceId);

//        viewDataUtility.removeStartAndEndNode(neo4jResult);

        List<GanttEntry> ganttEntries = new ArrayList<>();

        Map<InstanceActivity, List<InstanceActivity>> childParentRelationships = new HashMap<>();

        neo4jResult.stream().map(InstanceActivity::getFollowUpRelationships).flatMap(List::stream)
                .forEach(rel ->
                                childParentRelationships.computeIfAbsent(rel.getEndEvent(), k -> new ArrayList<>()).add(rel.getStartEvent())
                );

        neo4jResult.forEach(activity -> {

            if (activity instanceof InstanceIntermediateActivity) {
                DateTime startTime = ((InstanceIntermediateActivity) activity).getCreationTimeOrNull();
                DateTime endTime = ((InstanceIntermediateActivity) activity).getChangeTimeOrNull();

                if (startTime != null && endTime != null) {
                    DateTimeFormatter dateTimeOutput = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
                    String startTimeString = startTime.toString(dateTimeOutput);
                    String endTimeString = endTime.toString(dateTimeOutput);

                    GanttEntry ganttEntry = new GanttEntry();
                    ganttEntry.setId(activity.getId());
                    ganttEntry.setName(activity.getType());
                    ganttEntry.setParentId(0L);
                    ganttEntry.setStartDate(startTimeString);
                    ganttEntry.setPlanStartDate(startTimeString);
                    ganttEntry.setEndDate(endTimeString);
                    ganttEntry.setPlanEndDate(endTimeString);
                    ganttEntry.setCompletionPercentage(0);
                    ganttEntry.setStandardGroupTaskIsOpen(1);
                    ganttEntry.setCssClass("ggroupblack");

                    String dependsOn = childParentRelationships.get(activity).stream()
                            .map(act -> String.valueOf(act.getId())).collect(Collectors.joining(","));
                    ganttEntry.setDependsOnIds(dependsOn);

                    ganttEntries.add(ganttEntry);
                }
            }
        });

        return ganttEntries;
    }

}
