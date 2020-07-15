package at.ac.wuwien.causalminer.neo4jdb.services;

import at.ac.wuwien.causalminer.neo4jdb.configuration.LogExecutionTime;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.InstancesWithSameProcessModelResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.TotalDurationsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.repositories.ProcessInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional("graphTransactionManager")
public class ProcessInstanceServices {

    @Autowired
    private ProcessInstanceRepository processInstanceRepository;


    @LogExecutionTime
    public List<InstanceActivity> findAllCases() {
        return processInstanceRepository.findAllInstances();
    }

    @LogExecutionTime
    public InstanceActivity save(InstanceActivity event) {
        return processInstanceRepository.save(event);
    }

    @LogExecutionTime
    public Iterable<InstanceActivity> saveAll(List<InstanceActivity> events) {
        return processInstanceRepository.saveAll(events);
    }

    @LogExecutionTime
    public void deleteAll() {
        processInstanceRepository.deleteAll();
    }

    @LogExecutionTime
    public List<InstanceActivity> findByInstanceId(String instanceId) {
        return processInstanceRepository.findByInstanceId(instanceId);
    }

    @LogExecutionTime
    public List<TotalDurationsQueryResult> getTotalDurationsFromStartToEnd() {
        return processInstanceRepository.getTotalDurationsFromStartToEnd();
    }

    @LogExecutionTime
    public List<InstancesWithSameProcessModelResult> findAllInstancesWithSameProcessModel() {
        return processInstanceRepository.findAllInstancesWithSameProcessModel();
    }

}
