package at.ac.wuwien.causalminer.erpimport.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VEdaKRepository extends CrudRepository<VEdaKDto, VEdaKIdDto> {
}
