package at.ac.wuwien.causalminer.frontend.controller;

import at.ac.wuwien.causalminer.frontend.model.ModelCheckerButtonData;
import at.ac.wuwien.causalminer.frontend.configuration.NodeStyleProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Controller
public class ModelCheckerController {

    @Autowired
    private NodeStyleProperties properties;

    @Value("${dataTable.id.label}")
    private String dataTableIdLabel;

    @GetMapping(value = "process/model/checker")
    public String checker(Model model) {

        List<ModelCheckerButtonData> buttonData = new ArrayList<>();

        properties.getLabel().forEach((key, value) -> {
            if(!key.contains("null")) {
                buttonData.add(new ModelCheckerButtonData(value, properties.getColor().getOrDefault(key, properties.getDefaultColor())));
            }
        });

        buttonData.add(new ModelCheckerButtonData("Start", properties.getColor().getOrDefault("start", properties.getDefaultColor())));
        buttonData.add(new ModelCheckerButtonData("End", properties.getColor().getOrDefault("end", properties.getDefaultColor())));

        buttonData.sort(Comparator.comparing(t -> t.label));
        model.addAttribute("defaultModelButtonData", buttonData);

        model.addAttribute("dataTableIdLabel", dataTableIdLabel);
        return "modelChecker";
    }

}
