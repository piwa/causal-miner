package at.ac.wuwien.causalminer.erp2graphdb.services;

import at.ac.wuwien.causalminer.erp2graphdb.model.TransferredProcessInstance;
import at.ac.wuwien.causalminer.erp2graphdb.repositories.TransferredProcessInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional("erp2graphTransactionManager")
public class TransferredProcessInstanceService {

    @Autowired
    private TransferredProcessInstanceRepository transferredProcessInstanceRepository;

    public TransferredProcessInstance save(TransferredProcessInstance transferredProcessInstance) {
        return transferredProcessInstanceRepository.save(transferredProcessInstance);
    }

    public Iterable<TransferredProcessInstance> saveAll(Collection<TransferredProcessInstance> transferredProcessInstances) {
        return transferredProcessInstanceRepository.saveAll(transferredProcessInstances);
    }

    public void delete(TransferredProcessInstance transferredProcessInstance) {
        transferredProcessInstanceRepository.delete(transferredProcessInstance);
    }

    public void deleteAll() {
        transferredProcessInstanceRepository.deleteAll();
    }

    public Optional<TransferredProcessInstance> findTransferredProcessInstanceByInstanceId(UUID instanceId) {
        return transferredProcessInstanceRepository.findTransferredProcessInstanceByInstanceId(instanceId);
    }

    public Iterable<TransferredProcessInstance> findAll(int page, int size) {
        return transferredProcessInstanceRepository.findAll(PageRequest.of(page, size));
    }

    public Iterable<TransferredProcessInstance> findAll() {
        return transferredProcessInstanceRepository.findAll();
    }

//    public Iterable<TransferredProcessInstance> findViolatingInstances() {
//        return transferredProcessInstanceRepository.findViolatingInstances();
//    }

}
