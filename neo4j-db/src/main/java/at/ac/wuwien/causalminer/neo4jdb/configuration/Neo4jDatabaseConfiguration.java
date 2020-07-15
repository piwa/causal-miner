package at.ac.wuwien.causalminer.neo4jdb.configuration;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "at.ac.wuwien.causalminer.neo4jdb.domain")
@EnableNeo4jRepositories(sessionFactoryRef = "sessionFactory", transactionManagerRef = "graphTransactionManager", basePackages = "at.ac.wuwien.causalminer.neo4jdb.repositories")
public class Neo4jDatabaseConfiguration {

    @Value("${spring.datasource.neo4j.username}")
    private String username;
    @Value("${spring.datasource.neo4j.password}")
    private String password;
    @Value("${spring.datasource.neo4j.uri}")
    private String uri;

    @Bean
    public SessionFactory sessionFactory(org.neo4j.ogm.config.Configuration configuration) {
        return new SessionFactory(configuration, "at.ac.wuwien.causalminer.neo4jdb.domain");
    }

    @Bean(name = "graphTransactionManager")
    public Neo4jTransactionManager graphTransactionManager( SessionFactory sessionFactory) {
        return new Neo4jTransactionManager(sessionFactory);
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
                .uri(uri)
                .credentials(username, password)
                .build();
        return configuration;
    }
}
