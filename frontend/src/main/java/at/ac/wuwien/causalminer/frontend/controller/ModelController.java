package at.ac.wuwien.causalminer.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class ModelController {

    @Value("${dataTable.id.label}")
    private String dataTableIdLabel;

    @GetMapping(value = "process/model/global")
    public String global(Model model) {

        model.addAttribute("dataTableIdLabel", dataTableIdLabel);
        return "processModelGlobal";
    }

    @GetMapping(value = "process/model/combined")
    public String combined(Model model) {

        model.addAttribute("dataTableIdLabel", dataTableIdLabel);
        return "processModelCombined";
    }

    @GetMapping(value = "process/model/individual")
    public String individual(Model model) {

        model.addAttribute("dataTableIdLabel", dataTableIdLabel);
        return "processModelIndividual";
    }

}
