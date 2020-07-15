package at.ac.wuwien.causalminer.neo4jdb.configuration;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("at.ac.wuwien.causalminer.neo4jdb")
@EnableAspectJAutoProxy
@PropertySource("classpath:application_neo4j.properties")
@PropertySource("classpath:configuration/neo4j/neo4j_connection.properties")
@PropertySource("classpath:configuration/neo4j/neo4j_credentials.properties")
public class Neo4jMainConfiguration {
}
