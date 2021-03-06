package at.ac.wuwien.causalminer.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class AllCasesController {

    @Value("${dataTable.id.label}")
    private String dataTableIdLabel;

    @RequestMapping(value = "/cases/all", method = RequestMethod.GET)
    public String list(Model model) {

        model.addAttribute("dataTableIdLabel", dataTableIdLabel);

        return "allCases";
    }

}
