package at.ac.wuwien.causalminer.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ImportInstanceValidationController {

    @Value("${dataTable.id.label}")
    private String dataTableIdLabel;

    @GetMapping(value = "import/instance/validation")
    public String show(Model model) {

        model.addAttribute("dataTableIdLabel", dataTableIdLabel);
        return "importInstanceValidation";
    }
}
