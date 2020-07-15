package at.ac.wuwien.causalminer.neo4jdb.domain.instance;

import at.ac.wuwien.causalminer.neo4jdb.domain.IHomogeneousRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.TimePropertyUtility;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.neo4j.ogm.annotation.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@RelationshipEntity(type = "NULL_INSTANCE_FOLLOW_UP")
public class NullInstanceRelationship implements IHomogeneousRelationship<InstanceActivity> {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private InstanceActivity startEvent;

    @EndNode
    private InstanceActivity endEvent;

    @Property
    private Long duration;

    @Properties
    private Map<String, String> properties = new HashMap<>();

    public NullInstanceRelationship(InstanceActivity startEvent, InstanceActivity endEvent) {
        this.startEvent = startEvent;
        this.endEvent = endEvent;

        if(startEvent instanceof InstanceIntermediateActivity && endEvent instanceof InstanceIntermediateActivity) {
            DateTime startCreationTime = ((InstanceIntermediateActivity) startEvent).getCreationTimeOrNull();
            DateTime endCreationTime = ((InstanceIntermediateActivity) endEvent).getCreationTimeOrNull();
            Duration deltaT = new Duration(startCreationTime, endCreationTime);
            duration = deltaT.getMillis();
//            properties.put("delta_t", String.valueOf(deltaT.getMillis()));
        }
    }

    public Map<String, String> getProperties() {
        Map<String, String> returnValue = new HashMap<>();

        properties.forEach((k,v) -> returnValue.put(k, TimePropertyUtility.checkAndReturnTimeProperty(v)));

        if(duration != null) {
            Double totalDurationDays = ((double) duration) / 1000 / 60 / 60 / 24 ;
            DecimalFormat df = new DecimalFormat("#.00");
            returnValue.put("Duration-days", df.format(totalDurationDays));
        }

        returnValue.put("Duration", Optional.ofNullable(duration).orElse(0L).toString());

        return returnValue;
    }

    public String getLabel() {
        return "";
    }

    @Override
    public Boolean isAsymmetricConflictRelationship() {
        return false;
    }

    @Override
    public Boolean isAggregated() {
        return false;
    }

    @Override
    public Integer getCount() {
        return null;
    }
}
