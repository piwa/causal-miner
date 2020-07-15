package at.ac.wuwien.causalminer.erpimport.services;

import at.ac.wuwien.causalminer.erpimport.repositories.OrderRepository;
import at.ac.wuwien.causalminer.erpimport.model.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<OrderDto> findAll() {
        return StreamSupport.stream(orderRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public List<OrderDto> findByOrderNrIn(List<String> ids) {
        return orderRepository.findById_OrderNrIn(ids);
    }

    public List<OrderDto> findAllLimited(int page, int limit) {
        return orderRepository.findAll(PageRequest.of(page, limit)).getContent();
    }

}
