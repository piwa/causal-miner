package at.ac.wuwien.causalminer.neo4jdb.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;


@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("ModelNullActivity")
public class ModelNullActivity extends ModelActivity {

    public ModelNullActivity() {
    }

    public ModelNullActivity(String type) {
        super(type);
    }

    public ModelNullActivity(ModelActivity modelActivity) {
        super(modelActivity);
    }
}