package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import at.ac.wuwien.causalminer.neo4jdb.domain.TimePropertyUtility;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Properties;
import org.neo4j.ogm.annotation.Property;

import java.text.DecimalFormat;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("InstanceIntermediateActivity")
public class InstanceIntermediateActivity extends InstanceActivity {

    @Property
    private List<String> instanceIds = new ArrayList<>();

    @Properties
    private Map<String, String> origIds = new HashMap<>();

    @Properties
    private Map<String, String> origParameters = new HashMap<>();

    @Property
    private Long duration;

    public InstanceIntermediateActivity() {
    }

    public InstanceIntermediateActivity(String type) {
        super(type);
    }

    @Override
    public Map<String, String> getProperties() {

        Map<String, String> properties = super.getProperties();

        StringBuilder stringBuilder = new StringBuilder("[");
        instanceIds.forEach(id -> stringBuilder.append(id).append(","));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
        properties.put("Cases", stringBuilder.toString());

        properties.putAll(origIds);

        origParameters.forEach((k,v) -> {
            properties.put(k, TimePropertyUtility.checkAndReturnTimeProperty(v));
        });

        if(duration != null) {
            Double totalDurationDays = ((double) duration) / 1000 / 60 / 60 / 24 ;
            DecimalFormat df = new DecimalFormat("#.00");
            properties.put("Duration-days", df.format(totalDurationDays));
        }

        properties.put("Duration", Optional.ofNullable(duration).orElse(0L).toString());

        return properties;
    }

    public DateTime getCreationTimeOrNull() {
        String creationDateString = origParameters.get("creationDate");
        return stringToDateTime(creationDateString);
    }

    public DateTime getChangeTimeOrNull() {
        String changeDateString = origParameters.get("changeDate");
        return stringToDateTime(changeDateString);
    }

    private DateTime stringToDateTime(String timeString) {
        DateTimeFormatter dateTimeDatabase = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            return dateTimeDatabase.parseDateTime(timeString);
        } catch (Exception e) {
            return null;
        }
    }

    public void addInstanceIds(Set<UUID> instanceIds) {
        instanceIds.forEach(uuid -> this.instanceIds.add(uuid.toString()));
    }

    @Override
    public boolean isEssentialForProcessDepiction() {
        return true;
    }

    @Override
    public boolean isNullNode() {
        return false;
    }
}
