package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.ImportInstanceValidationController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = ImportInstanceValidationController.class)
public class ImportInstanceValidationControllerAdvice {

    @ModelAttribute("importInstanceValidationViewActiveSettings")
    public String cssActivePage() {
        return "active";
    }

}