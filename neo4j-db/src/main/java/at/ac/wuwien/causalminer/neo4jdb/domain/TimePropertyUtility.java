package at.ac.wuwien.causalminer.neo4jdb.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimePropertyUtility {

    public static String checkAndReturnTimeProperty(String timeProperty) {
        DateTimeFormatter dateTimeDatabase = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter dateTimeOutput = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            DateTime jodaTime = dateTimeDatabase.parseDateTime(timeProperty);
            return jodaTime.toString(dateTimeOutput);
        } catch (Exception e) {
            return timeProperty;
        }

    }

}
