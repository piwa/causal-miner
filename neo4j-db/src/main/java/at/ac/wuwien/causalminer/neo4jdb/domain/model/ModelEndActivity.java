package at.ac.wuwien.causalminer.neo4jdb.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;


@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("ModelEndActivity")
public class ModelEndActivity extends AbstractModelStartEndActivity {

    public ModelEndActivity() {
        super("End");
    }

    public ModelEndActivity(ModelActivity modelActivity) {
        super(modelActivity);
    }
}