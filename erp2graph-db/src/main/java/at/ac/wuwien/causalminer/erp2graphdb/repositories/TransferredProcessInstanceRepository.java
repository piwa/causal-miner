package at.ac.wuwien.causalminer.erp2graphdb.repositories;

import at.ac.wuwien.causalminer.erp2graphdb.model.TransferredProcessInstance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferredProcessInstanceRepository extends PagingAndSortingRepository<TransferredProcessInstance, Long> {

    Optional<TransferredProcessInstance> findTransferredProcessInstanceByInstanceId(UUID instanceId);

    @Query("SELECT p FROM TransferredProcessInstance AS p INNER JOIN p.violations v")
    Iterable<TransferredProcessInstance> findViolatingInstances();

}
