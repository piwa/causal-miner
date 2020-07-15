package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.ModelCompareController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = ModelCompareController.class)
public class ModelCompareControllerAdvice {

    @ModelAttribute("modelCompareViewActiveSettings")
    public String cssActivePage() {
        return "active";
    }

    @ModelAttribute("processModelViewActiveSettings")
    public String processModelViewActive() {
        return "active";
    }
}