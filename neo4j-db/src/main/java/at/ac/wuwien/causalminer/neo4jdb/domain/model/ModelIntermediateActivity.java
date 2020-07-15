package at.ac.wuwien.causalminer.neo4jdb.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.*;


@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("ModelIntermediateActivity")
public class ModelIntermediateActivity extends ModelActivity {

    public ModelIntermediateActivity() {
    }

    public ModelIntermediateActivity(String type) {
        super(type);
    }

    public ModelIntermediateActivity(ModelActivity modelActivity) {
        super(modelActivity);
    }
}