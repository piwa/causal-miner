package at.ac.wuwien.causalminer.frontend.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "node")
public class NodeStyleProperties {

    private Map<String, String> color;
    private String defaultColor;
    private Map<String, String> label;

}
