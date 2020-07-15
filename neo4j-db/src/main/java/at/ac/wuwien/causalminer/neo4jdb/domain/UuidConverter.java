package at.ac.wuwien.causalminer.neo4jdb.domain;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.util.UUID;

public class UuidConverter implements AttributeConverter<UUID, String> {

    @Override
    public String toGraphProperty(UUID uuid) {
        if(uuid == null) {
            return "";
        }
        return uuid.toString();
    }

    @Override
    public UUID toEntityAttribute(String uuidString) {
        if(StringUtils.isEmpty(uuidString)) {
            return null;
        }
        return UUID.fromString(uuidString);
    }
}
