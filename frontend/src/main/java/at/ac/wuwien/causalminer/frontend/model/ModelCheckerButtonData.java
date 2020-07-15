package at.ac.wuwien.causalminer.frontend.model;

import lombok.Data;

@Data
public class ModelCheckerButtonData {

    public String label;
    public String color;

    public ModelCheckerButtonData(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
