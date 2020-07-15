package at.ac.wuwien.causalminer.neo4jdb.services;

import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.io.fs.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public abstract class Neo4jEmbeddedDbTest {

    @Autowired
    private TestRepository testRepository;
    @Autowired
    private StartActivityServices startActivityServices;


    @AfterAll
    static void deleteDb(@Autowired TestRepository testRepository, @Autowired GraphDatabaseService graphDb, @Value("${embedded.graphDB.path.root}") String graphDbPath) {
        testRepository.emptyDB();
        graphDb.shutdown();

        try {
            FileUtils.deleteRecursively( new File(graphDbPath) );
        } catch (IOException e) {
            log.error("Exception while deleting the test database");
        }
    }

    @BeforeEach
    void emptyDb() {
        testRepository.emptyDB();
    }


    List<InstanceStartActivity> loadAllInstances() {
        testRepository.callApocRunFile("src/test/resources/allInstances.cypher");
        return getAndCheckProcessInstances(4);
    }

    List<InstanceStartActivity> loadSingleInstanceParallel() {
        testRepository.callApocRunFile("src/test/resources/singleInstanceParallel.cypher");
        return getAndCheckProcessInstances(1);
    }

    List<InstanceStartActivity> loadSingleInstanceSequence() {
        testRepository.callApocRunFile("src/test/resources/singleInstanceSequence.cypher");
        return getAndCheckProcessInstances(1);
    }

    List<InstanceStartActivity> loadTwoInstanceSharedNode() {
        testRepository.callApocRunFile("src/test/resources/twoInstanceSharedNode.cypher");
        return getAndCheckProcessInstances(2);
    }

    List<InstanceStartActivity> loadTwoInstancesCombinedModel() {
        testRepository.callApocRunFile("src/test/resources/twoInstancesCombinedModel.cypher");
        return getAndCheckProcessInstances(2);
    }

    private List<InstanceStartActivity> getAndCheckProcessInstances(int checkInstanceAmount) {
        List<InstanceStartActivity> startActivities = startActivityServices.findAll();
        assertThat(startActivities).hasSize(checkInstanceAmount);
        startActivities.forEach(act -> assertThat(act.getInstanceId()).isNotEmpty());
        return startActivities;
    }

}
