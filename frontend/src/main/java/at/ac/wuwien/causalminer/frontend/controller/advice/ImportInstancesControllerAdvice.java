package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.ImportInstancesController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = ImportInstancesController.class)
public class ImportInstancesControllerAdvice {

    @ModelAttribute("importInstancesViewActiveSettings")
    public String cssActivePage() {
        return "active";
    }

}