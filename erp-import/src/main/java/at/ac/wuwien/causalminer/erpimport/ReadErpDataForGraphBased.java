package at.ac.wuwien.causalminer.erpimport;

import at.ac.wuwien.causalminer.erpimport.model.OrderDto;
import at.ac.wuwien.causalminer.erpimport.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ReadErpDataForGraphBased {

    @Autowired
    private OrderService orderService;

    @Value("${test.erp.orderIds}")
    private List<String> fullOrderIds;

    @Value("${test.erp.order.byLimit}")
    private Boolean readOrderLimited;
    @Value("${test.erp.order.byLimit.limit}")
    private Integer orderLimit;
    @Value("${test.erp.order.byLimit.page}")
    private Integer orderPage;


    @Transactional("erpTransactionManager")
    public List<OrderDto> readErpDatabase() {

        if(readOrderLimited) {
            List<OrderDto> allOrderDto = orderService.findAllLimited(orderPage, orderLimit);
            return allOrderDto;
        }
        else {
            List<OrderDto> allOrderDto = orderService.findByOrderNrIn(fullOrderIds);
            return allOrderDto;
        }

    }



}
