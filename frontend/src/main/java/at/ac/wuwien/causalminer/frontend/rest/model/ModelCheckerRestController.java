package at.ac.wuwien.causalminer.frontend.rest.model;

import at.ac.wuwien.causalminer.frontend.model.cytoscape.*;
import at.ac.wuwien.causalminer.frontend.model.tables.ProcessViewTableEntry;
import at.ac.wuwien.causalminer.frontend.rest.CytoscapeTreeUtility;
import at.ac.wuwien.causalminer.frontend.configuration.NodeStyleProperties;
import at.ac.wuwien.causalminer.frontend.model.cytoscape.*;
import at.ac.wuwien.causalminer.frontend.model.paging.ViewPage;
import at.ac.wuwien.causalminer.frontend.rest.ViewDataUtility;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.ModelCheckerResult;
import at.ac.wuwien.causalminer.neo4jdb.services.ProcessModelServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/process/model/checker")
public class ModelCheckerRestController {

    @Autowired
    private ProcessModelServices processModelServices;
    @Autowired
    private ViewDataUtility viewDataUtility;
    @Autowired
    private NodeStyleProperties nodeStyleProperties;




    @PostMapping(value = "complete/notMatching/table")
    public ViewPage<ProcessViewTableEntry> getCompleteNotMatchingModelsTable(@RequestBody CytoscapeElement cytoscapeElement) {
        Function<List<List<String>>,List<ModelCheckerResult>> queryFunction = (labelPath) -> processModelServices.findCompleteNotMatchingProcessModels(labelPath);
        return perform(cytoscapeElement, queryFunction);
    }

    @PostMapping(value = "complete/matching/table")
    public ViewPage<ProcessViewTableEntry> getCompleteMatchingModelsTable(@RequestBody CytoscapeElement cytoscapeElement) {
        Function<List<List<String>>,List<ModelCheckerResult>> queryFunction = (labelPath) -> processModelServices.findCompleteMatchingProcessModels(labelPath);
        return perform(cytoscapeElement, queryFunction);
    }

    @PostMapping(value = "partial/notMatching/table")
    public ViewPage<ProcessViewTableEntry> getPartialNotMatchingModelsTable(@RequestBody CytoscapeElement cytoscapeElement) {
        Function<List<List<String>>,List<ModelCheckerResult>> queryFunction = (labelPath) -> processModelServices.findPartialNotMatchingProcessModels(labelPath);
        return perform(cytoscapeElement, queryFunction);
    }

    @PostMapping(value = "partial/notMatching/userFunction/table")
    public ViewPage<ProcessViewTableEntry> getPartialNotMatchingModelsUserFunctionTable(@RequestBody CytoscapeElement cytoscapeElement) {
        Function<List<List<String>>,List<ModelCheckerResult>> queryFunction = (labelPath) -> processModelServices.findPartialNotMatchingProcessModelsUserFunction(labelPath);
        return perform(cytoscapeElement, queryFunction);
    }

    @PostMapping(value = "partial/matching/table")
    public ViewPage<ProcessViewTableEntry> getPartialMatchingModelsTable(@RequestBody CytoscapeElement cytoscapeElement) {

        Function<List<List<String>>,List<ModelCheckerResult>> queryFunction = (labelPath) -> processModelServices.findPartialMatchingProcessModels(labelPath);
        return perform(cytoscapeElement, queryFunction);
    }

    private ViewPage<ProcessViewTableEntry> perform(CytoscapeElement cytoscapeElement, Function<List<List<String>>,List<ModelCheckerResult>> queryFunction) {
        CytoscapeTreeUtility cytoscapeTreeUtility = new CytoscapeTreeUtility(nodeStyleProperties.getLabel());
        List<List<String>> labelPath = cytoscapeTreeUtility.getLabelsOfAllPaths(cytoscapeElement);

        ViewPage<ProcessViewTableEntry> viewPage = new ViewPage<>();
        if (labelPath.size() > 0) {
            List<ModelCheckerResult> modelCheckerResults = queryFunction.apply(labelPath);
            Set<String> strings = modelCheckerResults.stream().map(ModelCheckerResult::getInstanceId).collect(Collectors.toSet());
            List<ProcessViewTableEntry> caseTableEntries = viewDataUtility.getProcessViewTableEntries(strings);
            viewPage = new ViewPage<>(caseTableEntries);
        }

        return viewPage;
    }


    @GetMapping(value = "loadTemplate")
    public CytoscapeElement loadTemplate() {
        // create template

        return cytoscapeElement;
    }


    private CytoscapeNodeData getCytoscapeNodeData(String id, String label) {
        CytoscapeNodeData nodeData = new CytoscapeNodeData(id, nodeStyleProperties.getLabel().getOrDefault(label.toLowerCase(), label), label);
        nodeData.setColor(nodeStyleProperties.getColor().getOrDefault(label.toLowerCase(), nodeStyleProperties.getDefaultColor()));
        return nodeData;
    }

}
