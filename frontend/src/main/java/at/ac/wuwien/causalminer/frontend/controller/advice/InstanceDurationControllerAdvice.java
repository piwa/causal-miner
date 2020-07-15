package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.InstanceDurationsController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = InstanceDurationsController.class)
public class InstanceDurationControllerAdvice {

    @ModelAttribute("instanceDurationViewActiveSetting")
    public String instanceDurationViewActiveSetting() {
        return "active";
    }

}