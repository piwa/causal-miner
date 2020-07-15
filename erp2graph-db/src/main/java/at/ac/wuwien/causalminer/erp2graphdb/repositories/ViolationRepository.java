package at.ac.wuwien.causalminer.erp2graphdb.repositories;

import at.ac.wuwien.causalminer.erp2graphdb.model.Violation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationRepository extends CrudRepository<Violation, Integer> {
}

