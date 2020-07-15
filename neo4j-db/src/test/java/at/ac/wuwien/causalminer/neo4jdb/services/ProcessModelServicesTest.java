package at.ac.wuwien.causalminer.neo4jdb.services;

import at.ac.wuwien.causalminer.neo4jdb.configuration.Neo4jDatabaseConfiguration;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.AbstractInstanceStartEndActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelActivity;
import at.ac.wuwien.causalminer.neo4jdb.configuration.Neo4jMainConfiguration;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.model.ModelStartActivity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@DataNeo4jTest(excludeFilters = @ComponentScan.Filter({Neo4jMainConfiguration.class, Neo4jDatabaseConfiguration.class}))
@ContextConfiguration(classes = {TestConfiguration.class})
@Slf4j
@Transactional("graphTransactionManager")
class ProcessModelServicesTest extends Neo4jEmbeddedDbTest {

    @Autowired
    private ProcessModelServices processModelServices;
    @Autowired
    private StartActivityServices startActivityServices;

    @Test
    public void createAndCheckIndividualProcessModels_SequencialProcessInstance_success() {

        List<InstanceStartActivity> startActivities = loadSingleInstanceSequence();

        startActivities.forEach(act -> {
            List<ModelActivity> modelActivities = processModelServices.createIndividualProcessModelForInstanceId(act.getInstanceId());
            processModelServices.saveAll(modelActivities);
        });

        List<ModelStartActivity> individualProcessModelStart = processModelServices.findAllIndividualProcessModels();
        assertThat(individualProcessModelStart).hasSize(1);
//        individualProcessModelStart.forEach(act -> assertThat(act.getModelId()).isNotEmpty());
    }

    @Test
    public void createAndCheckIndividualProcessModels_ParallelProcessInstance_success() {

        List<InstanceStartActivity> startActivities = loadSingleInstanceParallel();

        startActivities.forEach(act -> {
            List<ModelActivity> modelActivities = processModelServices.createIndividualProcessModelForInstanceId(act.getInstanceId());
            processModelServices.saveAll(modelActivities);
        });

        List<ModelStartActivity> individualProcessModelStart = processModelServices.findAllIndividualProcessModels();
        assertThat(individualProcessModelStart).hasSize(1);
//        individualProcessModelStart.forEach(act -> assertThat(act.getModelId()).isNotEmpty());
    }



    @Test
    public void createAndCheckCombinedProcessModels_success() {

        List<InstanceStartActivity> startActivities = loadTwoInstancesCombinedModel();

        List<String> instanceIds = startActivities.stream().map(AbstractInstanceStartEndActivity::getInstanceId).collect(Collectors.toList());

        List<ModelActivity> modelActivities = processModelServices.createCombinedProcessModelForInstanceIds(instanceIds);
        processModelServices.saveAll(modelActivities);

        List<ModelStartActivity> combinedProcessModelStart = processModelServices.findAllCombinedProcessModels();
        assertThat(combinedProcessModelStart).hasSize(1);
//        individualProcessModelStart.forEach(act -> assertThat(act.getModelId()).isNotEmpty());
    }
}