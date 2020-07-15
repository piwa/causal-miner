package at.ac.wuwien.causalminer.erp2graphdb.services;

import at.ac.wuwien.causalminer.erp2graphdb.model.Violation;
import at.ac.wuwien.causalminer.erp2graphdb.repositories.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional("erp2graphTransactionManager")
public class ViolationService {

    @Autowired
    private ViolationRepository violationRepository;

    public Violation save(Violation violation) {
        return violationRepository.save(violation);
    }

    public void delete(Violation violation) {
        violationRepository.delete(violation);
    }

}
