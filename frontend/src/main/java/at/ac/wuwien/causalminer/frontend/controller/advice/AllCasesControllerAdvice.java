package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.AllCasesController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = AllCasesController.class)
public class AllCasesControllerAdvice {

    @ModelAttribute("allCasesActiveSettings")
    public String cssActivePage() {
        return "active";
    }

}
