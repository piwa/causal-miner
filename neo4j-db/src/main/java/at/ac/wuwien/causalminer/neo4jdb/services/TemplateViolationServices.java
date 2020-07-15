package at.ac.wuwien.causalminer.neo4jdb.services;

import at.ac.wuwien.causalminer.neo4jdb.configuration.LogExecutionTime;
import at.ac.wuwien.causalminer.neo4jdb.domain.IHomogeneousRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.input.AsymmetricConflictInput;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.input.CardinalityInputVariables;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.EndCardinalityViolationsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.StartCardinalityViolationsQueryResult;
import at.ac.wuwien.causalminer.neo4jdb.repositories.TemplateViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional("graphTransactionManager")
public class TemplateViolationServices {

    @Autowired
    private TemplateViolationRepository templateViolationRepository;


    @LogExecutionTime
    public List<InstanceRelationship> findAllTemporalRelationshipViolations() {
        return templateViolationRepository.findAllTemporalRelationshipViolations();
    }

    @LogExecutionTime
    public List<EndCardinalityViolationsQueryResult> findAllEndCardinalityViolations(List<CardinalityInputVariables> inputVariables) {
        return templateViolationRepository.findAllEndCardinalityViolations(inputVariables);
    }

    @LogExecutionTime
    public List<StartCardinalityViolationsQueryResult> findAllStartCardinalityViolations(List<CardinalityInputVariables> inputVariables) {
        return templateViolationRepository.findAllStartCardinalityViolations(inputVariables);
    }

    @LogExecutionTime
    public List<IHomogeneousRelationship<InstanceActivity>> findAllAsymmetricConflictViolations(List<AsymmetricConflictInput> inputVariables) {
        return templateViolationRepository.findAllAsymmetricConflictViolations(inputVariables);
    }


}
