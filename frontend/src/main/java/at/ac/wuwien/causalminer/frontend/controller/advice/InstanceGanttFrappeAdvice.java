package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.InstanceGanttFrappe;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = InstanceGanttFrappe.class)
public class InstanceGanttFrappeAdvice {

    @ModelAttribute("instanceGanttFrappeViewActiveSettings")
    public String cssActivePage() {
        return "active";
    }

}