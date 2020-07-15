package at.ac.wuwien.causalminer.neo4jdb.services;

import at.ac.wuwien.causalminer.neo4jdb.configuration.LogExecutionTime;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.repositories.InstanceStartActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Transactional("graphTransactionManager")
public class StartActivityServices {

    @Autowired
    private InstanceStartActivityRepository instanceStartActivityRepository;


    @LogExecutionTime
    public List<InstanceStartActivity> findAll() {
        return StreamSupport.stream(instanceStartActivityRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @LogExecutionTime
    public List<InstanceStartActivity> findAll(int page, int size) {
        return StreamSupport.stream(instanceStartActivityRepository.findAll(PageRequest.of(page, size)).spliterator(), false).collect(Collectors.toList());
    }

    @LogExecutionTime
    public InstanceStartActivity save(InstanceStartActivity startActivity) {
        return instanceStartActivityRepository.save(startActivity);
    }

    @LogExecutionTime
    public Iterable<InstanceStartActivity> saveAll(Collection<InstanceStartActivity> startActivity) {
        return instanceStartActivityRepository.saveAll(startActivity);
    }

    @LogExecutionTime
    public List<InstanceStartActivity> findAllByInstanceId(Collection<String> instanceIds) {
        return instanceStartActivityRepository.findAllByInstanceIdIsIn(instanceIds);
    }

    @LogExecutionTime
    public List<InstanceStartActivity> findByModelId(String modelId) {
        return instanceStartActivityRepository.findByModelId(modelId);
    }
}
