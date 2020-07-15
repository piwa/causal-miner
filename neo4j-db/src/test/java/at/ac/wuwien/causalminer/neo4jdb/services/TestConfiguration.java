package at.ac.wuwien.causalminer.neo4jdb.services;

import apoc.cypher.Cypher;
import apoc.cypher.CypherFunctions;
import apoc.cypher.Timeboxed;
import apoc.text.Strings;
import apoc.util.Utils;
import at.ac.wuwien.causalminer.neo4jdb.configuration.Neo4jDatabaseConfiguration;
import at.ac.wuwien.causalminer.neo4jdb.configuration.Neo4jMainConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.graphdb.DependencyResolver;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.config.Setting;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.kernel.impl.proc.Procedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;

import static org.neo4j.kernel.configuration.Settings.BOOLEAN;
import static org.neo4j.kernel.configuration.Settings.setting;


@Configuration
@ComponentScan(basePackages = {"at.ac.wuwien.causalminer.neo4jdb"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {Neo4jMainConfiguration.class, Neo4jDatabaseConfiguration.class}))
@EnableNeo4jRepositories(sessionFactoryRef = "sessionFactory", transactionManagerRef = "graphTransactionManager", basePackages = {"at.ac.wuwien.causalminer.neo4jdb.repositories", "at.ac.wuwien.erp4cloud.erp2graph.neo4jdb.services"})
@EnableTransactionManagement
@AutoConfigurationPackage
@PropertySource("classpath:application.properties")
@Slf4j
public class TestConfiguration {

    @Value("${embedded.graphDB.path}")
    private String graphDbPath;

    @Bean
    public GraphDatabaseService graphDatabaseService() {

        Setting<Boolean> apocImportFileEnabled = setting( "apoc.import.file.enabled", BOOLEAN, "true" );
        GraphDatabaseService db = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(new File(graphDbPath))
                .setConfig(apocImportFileEnabled, "true")
                .setConfig(GraphDatabaseSettings.transaction_timeout, "360s")
                .setConfig(GraphDatabaseSettings.transaction_start_timeout, "10s")
                .setConfig(GraphDatabaseSettings.shutdown_transaction_end_timeout, "10s")
                .newGraphDatabase();

        try {
            registerProcedure(db, Cypher.class, Utils.class, CypherFunctions.class, Timeboxed.class, Strings.class,
                    at.ac.wuwien.causalminer.neo4jPlugin.EqualLabelPathsFunctions.class, at.ac.wuwien.causalminer.neo4jPlugin.ExtendedGrouping.class);
        } catch (KernelException e) {
            log.error("Exception while loading the APOC procedures");
        }

        return db;
    }

    private void registerProcedure(GraphDatabaseService db, Class<?>... procedures) throws KernelException {
        Procedures proceduresService = ((GraphDatabaseAPI) db).getDependencyResolver().resolveDependency(Procedures.class, DependencyResolver.SelectionStrategy.ONLY);
        for (Class<?> procedure : procedures) {
            proceduresService.registerProcedure(procedure);
            proceduresService.registerFunction(procedure);
        }
    }

    @Bean
    public SessionFactory sessionFactory(GraphDatabaseService graphDatabaseService, org.neo4j.ogm.config.Configuration configuration) throws Throwable {
        EmbeddedDriver driver = new EmbeddedDriver(graphDatabaseService, configuration);
        return new SessionFactory(driver, "at.ac.wuwien.causalminer.neo4jdb.domain");
    }

    @Bean(name = "graphTransactionManager")
    public Neo4jTransactionManager graphTransactionManager(SessionFactory sessionFactory) {
        return new Neo4jTransactionManager(sessionFactory);
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        return new org.neo4j.ogm.config.Configuration.Builder().build();
    }

}
