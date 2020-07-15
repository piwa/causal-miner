package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import at.ac.wuwien.causalminer.neo4jdb.domain.DateTimeConverter;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.StartEndNodeWithoutRelationships;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractInstanceStartEndActivity extends InstanceActivity implements StartEndNodeWithoutRelationships {

    @Property(name = "instanceId")
    private String instanceId;

    @Convert(DateTimeConverter.class)
    private DateTime timestamp;

    public AbstractInstanceStartEndActivity() {
    }

    public AbstractInstanceStartEndActivity(String type) {
        super(type);
    }

    @Override
    public Map<String, String> getProperties() {


        Map<String, String> properties = super.getProperties();
        properties.put("Instance ID", instanceId);

        DateTimeFormatter dateTimeOutput = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        properties.put("Timestamp", dateTimeOutput.print(timestamp));

        return properties;
    }

    @Override
    public boolean isEssentialForProcessDepiction() {
        return false;
    }

    @Override
    public boolean isNullNode() {
        return false;
    }

}
