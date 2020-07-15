package at.ac.wuwien.causalminer.erp2graphdb.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:application_erp2graph_db.properties")
@PropertySource("classpath:application.properties")
public class GeneralConfiguration {
}
