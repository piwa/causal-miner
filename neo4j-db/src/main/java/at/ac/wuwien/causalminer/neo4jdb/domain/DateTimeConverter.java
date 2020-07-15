package at.ac.wuwien.causalminer.neo4jdb.domain;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.text.AttributedCharacterIterator;

public class DateTimeConverter implements AttributeConverter<DateTime, String> {

    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public String toGraphProperty(DateTime dateTime) {
        if(dateTime == null) {
            return "";
        }
        return dateTime.toString();
    }

    @Override
    public DateTime toEntityAttribute(String dateTimeString) {
        if(StringUtils.isEmpty(dateTimeString)) {
            return new DateTime(0);
        }
        return dateTimeFormatter.parseDateTime(dateTimeString);
    }
}
