package at.ac.wuwien.causalminer.neo4jdb.services;

import at.ac.wuwien.causalminer.neo4jdb.configuration.Neo4jDatabaseConfiguration;
import at.ac.wuwien.causalminer.neo4jdb.configuration.Neo4jMainConfiguration;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceEndActivity;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceRelationship;
import at.ac.wuwien.causalminer.neo4jdb.domain.instance.InstanceStartActivity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataNeo4jTest(excludeFilters = @ComponentScan.Filter({Neo4jMainConfiguration.class, Neo4jDatabaseConfiguration.class}))
@ContextConfiguration(classes = {TestConfiguration.class})
@Slf4j
class ProcessInstanceServicesTest {

    @Autowired
    private ProcessInstanceServices processInstanceServices;


    @BeforeAll
    static void loadTestData(@Autowired SessionFactory sessionFactory, @Autowired GraphDatabaseService graphDatabaseService) {

        Session session = sessionFactory.openSession();
        session.query("CALL apoc.cypher.runFile('src/test/resources/twoInstancesCombinedModel.cypher')", new HashMap<>());
    }

    @AfterAll
    static void emptyDb(@Autowired SessionFactory sessionFactory, @Autowired GraphDatabaseService graphDb, @Value("${embedded.graphDB.path.root}") String graphDbPath) {
        Session session = sessionFactory.openSession();
        session.query("MATCH (n) DETACH DELETE n;", new HashMap<>());
        graphDb.shutdown();
        session.clear();

        try {
            FileUtils.deleteRecursively( new File(graphDbPath) );
        } catch (IOException e) {
            log.error("Exception while deleting the test database");
        }
    }

    @Test
    void saveAndFindAll_success() {
        List<InstanceActivity> persistedEvents1 = processInstanceServices.findAllCases();
        log.info(persistedEvents1.size() + "");

        InstanceActivity instance1Start = new InstanceStartActivity();
        InstanceActivity instance1End = new InstanceEndActivity();
        InstanceActivity instance2Start = new InstanceStartActivity();
        InstanceActivity instance2End = new InstanceEndActivity();

        InstanceRelationship followup1 = new InstanceRelationship(instance1Start, instance1End);
        instance1Start.getFollowUpRelationships().add(followup1);
        InstanceRelationship followup2 = new InstanceRelationship(instance2Start, instance2End);
        instance2Start.getFollowUpRelationships().add(followup2);

        List<InstanceActivity> activities = new ArrayList<>();
        activities.add(instance1Start);
        activities.add(instance1End);
        activities.add(instance2Start);
        activities.add(instance2End);
        processInstanceServices.saveAll(activities);

        List<InstanceActivity> persistedEvents = processInstanceServices.findAllCases();

        assertThat(persistedEvents).hasSize(29);
    }

}