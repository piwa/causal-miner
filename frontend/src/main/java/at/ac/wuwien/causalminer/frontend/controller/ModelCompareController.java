package at.ac.wuwien.causalminer.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class ModelCompareController {

    @Value("${dataTable.id.label}")
    private String dataTableIdLabel;

    @GetMapping(value = "process/model/comparer")
    public String compare(Model model) {

        model.addAttribute("dataTableIdLabel", dataTableIdLabel);
        return "modelComparer";
    }

}
