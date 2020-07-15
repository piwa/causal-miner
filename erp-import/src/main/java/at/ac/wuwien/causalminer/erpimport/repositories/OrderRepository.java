package at.ac.wuwien.causalminer.erpimport.repositories;

import at.ac.wuwien.causalminer.erpimport.model.OrderDto;
import at.ac.wuwien.causalminer.erpimport.model.OrderIdDto;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<OrderDto, OrderIdDto> {

    List<OrderDto> findById_OrderNrIn(List<String> ids);

}
