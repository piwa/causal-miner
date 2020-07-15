package at.ac.wuwien.causalminer.erpimport.model;

import org.joda.time.DateTime;

public interface ErpData {

    ErpId getId();

    DateTime getCreationDate();

    void setCreationDate(DateTime creationDate);

    String getCreationUser();

    DateTime getChangeDate();

    String getChangeUser();
}
