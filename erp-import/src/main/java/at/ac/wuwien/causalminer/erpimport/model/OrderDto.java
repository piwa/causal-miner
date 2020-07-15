package at.ac.wuwien.causalminer.erpimport.model;

import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceIntermediateActivity;
import at.ac.wuwien.causalminer.transformer.model.INode;
import at.ac.wuwien.causalminer.transformer.model.Node;
import at.ac.wuwien.causalminer.transformer.model.NullNode;
import at.ac.wuwien.causalminer.transformer.model.EventBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="ORDER")
@AttributeOverrides({
        @AttributeOverride(name = "creationDate", column = @Column(name = "ORDER_DTANLAG", columnDefinition = "nvarchar")),
        @AttributeOverride(name = "creationUser", column = @Column(name = "ORDER_USERANLAG", columnDefinition = "nvarchar")),
        @AttributeOverride(name = "changeDate", column = @Column(name = "ORDER_DTAENDG", columnDefinition = "nvarchar")),
        @AttributeOverride(name = "changeUser", column = @Column(name = "ORDER_USERAENDG", columnDefinition = "nvarchar")),
})
public class OrderDto extends ErpGlobalVariablesParameters implements ErpData, EventBuilder, GraphNodeBuilder {

    public final static String TYPE = "ORDER";

    @EmbeddedId
    private OrderIdDto id;

    @Column(name = "Order_DTAUFTRAG")
    private DateTime jobDate;

    // parents
//    @OneToMany(mappedBy = "orderDto", fetch = FetchType.EAGER)
//    private Set<OrderParentDto> OrderParentDtos;

    // children
    @OneToMany(mappedBy = "orderDto")
    private List<SingleItemsSpearationDto> singleItemsSpearationDtos = new ArrayList<>();


    @Override
    public INode getNode(INode parentNode, UUID instanceId) {

        Node currentNode = getCurrentNode(parentNode, instanceId, TYPE + "_" + id.getOrderNr(), TYPE, id, this);

        if(singleItemsSpearationDtos == null || singleItemsSpearationDtos.size() == 0) {
            currentNode.addChild(NullNode.of(currentNode, instanceId, singleItemsSpearationDtos.TYPE));
        }
        else {
            for (singleItemsSpearationDtos singleItemsSpearationDto : singleItemsSpearationDtos) {
                currentNode.addChild(singleItemsSpearationDto.getNode(currentNode, instanceId));
            }
        }

        return currentNode;
    }

    @Override
    protected InstanceIntermediateActivity getEvent() {
        return new InstanceIntermediateActivity(TYPE);
    }


}
