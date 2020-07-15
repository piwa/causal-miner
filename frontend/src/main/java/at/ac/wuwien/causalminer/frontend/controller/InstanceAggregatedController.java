package at.ac.wuwien.causalminer.frontend.controller;

import at.ac.wuwien.causalminer.frontend.configuration.NodeStyleProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class InstanceAggregatedController {

    @Value("${dataTable.id.label}")
    private String dataTableIdLabel;

    @Autowired
    private NodeStyleProperties nodeStyleProperties;

    @GetMapping(value = "process/instance/aggregated")
    public String show(Model model) {

        List<String> filteredEventTypes = nodeStyleProperties.getLabel().values().stream().filter(label -> !label.toLowerCase().contains("null")).collect(Collectors.toList());
        filteredEventTypes.sort(Comparator.naturalOrder());
        model.addAttribute("eventTypes", filteredEventTypes);

        model.addAttribute("dataTableIdLabel", dataTableIdLabel);
        return "processInstanceAggregated";
    }



}
