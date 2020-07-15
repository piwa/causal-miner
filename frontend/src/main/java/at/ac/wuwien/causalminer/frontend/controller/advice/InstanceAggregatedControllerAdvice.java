package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.InstanceAggregatedController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = InstanceAggregatedController.class)
public class InstanceAggregatedControllerAdvice {

    @ModelAttribute("instanceAggregatedViewActiveSettings")
    public String cssActivePage() {
        return "active";
    }

}