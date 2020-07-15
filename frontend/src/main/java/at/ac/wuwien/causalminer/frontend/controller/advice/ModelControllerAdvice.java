package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.ModelController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = ModelController.class)
public class ModelControllerAdvice {

    @ModelAttribute("modelControllerViewActiveSettings")
    public String modelControllerViewActive() {
        return "active";
    }

    @ModelAttribute("processModelViewActiveSettings")
    public String processModelViewActive() {
        return "active";
    }
}