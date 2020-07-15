package at.ac.wuwien.causalminer.frontend.controller.advice;

import at.ac.wuwien.causalminer.frontend.controller.DriftAnalysisController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = DriftAnalysisController.class)
public class DriftAnalysisControllerAdvice {

    @ModelAttribute("analysisViewActiveSettings")
    public String analysisViewActive() {
        return "active";
    }

}