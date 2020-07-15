package at.ac.wuwien.causalminer.erp2graphdb;

import at.ac.wuwien.causalminer.erp2graphdb.model.TransferredProcessInstance;
import at.ac.wuwien.causalminer.erp2graphdb.model.Violation;
import at.ac.wuwien.causalminer.erp2graphdb.services.TransferredProcessInstanceService;
import at.ac.wuwien.causalminer.erp2graphdb.model.ViolatingNode;
import at.ac.wuwien.causalminer.erp2graphdb.model.ViolationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class Erp2GraphApplicationTests {

    @Autowired
    private TransferredProcessInstanceService transferredProcessInstanceService;

    @Test
    @Transactional("erp2graphTransactionManager")
    public void testSaveToDb_withoutViolations() {

        UUID instanceId = UUID.randomUUID();
        TransferredProcessInstance transferredProcessInstance = new TransferredProcessInstance(instanceId, "rootNoteId");

        TransferredProcessInstance storedElement = transferredProcessInstanceService.save(transferredProcessInstance);
        assertThat(storedElement.getId()).isNotNull();

        Optional<TransferredProcessInstance> foundInDb = transferredProcessInstanceService.findTransferredProcessInstanceByInstanceId(instanceId);
        assertThat(foundInDb).isPresent();

        transferredProcessInstanceService.delete(foundInDb.get());

        Optional<TransferredProcessInstance> foundInDbAfterDelete = transferredProcessInstanceService.findTransferredProcessInstanceByInstanceId(instanceId);
        assertThat(foundInDbAfterDelete).isEmpty();

    }

    @Test
    @Transactional("erp2graphTransactionManager")
    public void testSaveToDb() {

        UUID instanceId = UUID.randomUUID();
        TransferredProcessInstance transferredProcessInstance = new TransferredProcessInstance(instanceId, "rootNoteId");

        Violation newViolation1 = new Violation(new ViolatingNode(1L, "originalId 1", "originalId 1"), new ViolatingNode(2L,"originalId 1", "originalId 1"), ViolationType.Cardinality);
        Violation newViolation2 = new Violation(new ViolatingNode(3L,"originalId 2", "originalId 2"), new ViolatingNode(4L, "originalId 2", "originalId 2"), ViolationType.Asymmetry);
        transferredProcessInstance.getViolations().add(newViolation1);
        transferredProcessInstance.getViolations().add(newViolation2);

        TransferredProcessInstance storedElement = transferredProcessInstanceService.save(transferredProcessInstance);
        assertThat(storedElement.getId()).isNotNull();

        Optional<TransferredProcessInstance> foundInDb = transferredProcessInstanceService.findTransferredProcessInstanceByInstanceId(instanceId);
        assertThat(foundInDb).isPresent();
        assertThat(foundInDb.get().getViolations()).hasSize(2);

//        transferredProcessInstanceService.delete(foundInDb.get());

//        Optional<TransferredProcessInstance> foundInDbAfterDelete = transferredProcessInstanceService.findTransferredProcessInstanceByInstanceId(instanceId);
//        assertThat(foundInDbAfterDelete).isEmpty();

    }

}
