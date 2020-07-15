package at.ac.wuwien.causalminer.neo4jdb.domain.model;

import at.ac.wuwien.causalminer.neo4jdb.domain.instance.OfRelationship;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("ModelStartActivity")
public class ModelStartActivity extends AbstractModelStartEndActivity {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "INSTANCE_OF", direction = Relationship.INCOMING)
    private List<OfRelationship> instanceOfRelationships = new ArrayList<>();

    public ModelStartActivity() {
        super("Start");
    }

    public ModelStartActivity(ModelActivity modelActivity) {
        super(modelActivity);
    }
}