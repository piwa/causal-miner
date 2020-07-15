package at.ac.wuwien.causalminer.erpimport.model;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Embeddable
public class OrderIdDto implements ErpId, Serializable {

    @NotNull
    @Column(name = "Order_NR", columnDefinition = "nvarchar")
    private String orderNr;

    @Override
    public Map<String, String> getEventIds() {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("OrderNr", this.getOrderNr());
        return returnMap;
    }

}
