package at.ac.wuwien.causalminer.neo4jdb.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.Property;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractModelStartEndActivity extends ModelActivity {

    @Property(name = "modelId")
    private String modelId;

    public AbstractModelStartEndActivity(String type) {
        super(type);
    }

    public AbstractModelStartEndActivity(ModelActivity modelActivity) {
        super(modelActivity);
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> properties = super.getProperties();
        properties.put("Model Id", modelId);
        return properties;
    }

    @Override
    public boolean isEssentialForProcessDepiction() {
        return false;
    }
}
