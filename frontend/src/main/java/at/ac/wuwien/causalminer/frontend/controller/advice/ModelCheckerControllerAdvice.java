package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.ModelCheckerController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = ModelCheckerController.class)
public class ModelCheckerControllerAdvice {

    @ModelAttribute("modelCheckerViewActiveSettings")
    public String cssActivePage() {
        return "active";
    }

    @ModelAttribute("processModelViewActiveSettings")
    public String processModelViewActive() {
        return "active";
    }
}