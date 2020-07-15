package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.InstanceController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = InstanceController.class)
public class InstanceControllerAdvice {

    @ModelAttribute("instanceViewActiveSettings")
    public String cssActivePage() {
        return "active";
    }

}