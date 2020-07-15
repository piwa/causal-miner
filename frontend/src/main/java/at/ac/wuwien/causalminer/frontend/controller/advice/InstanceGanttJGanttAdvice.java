package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.InstanceGanttJGantt;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = InstanceGanttJGantt.class)
public class InstanceGanttJGanttAdvice {

    @ModelAttribute("instanceGanttJGanttViewActiveSettings")
    public String cssActivePage() {
        return "active";
    }

}