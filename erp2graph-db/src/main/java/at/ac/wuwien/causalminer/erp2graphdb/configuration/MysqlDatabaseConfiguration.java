package at.ac.wuwien.causalminer.erp2graphdb.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

//@Profile({ "erp2graph_mysql" })
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "at.ac.wuwien.causalminer.erp2graphdb.repositories", entityManagerFactoryRef = "erp2graphEntityManager", transactionManagerRef = "erp2graphTransactionManager")
@PropertySources({
        @PropertySource("classpath:configuration/mysql/db_connection.properties"),
        @PropertySource("classpath:configuration/mysql/db_credentials.properties"),
})
public class MysqlDatabaseConfiguration {

    @Value("${spring.datasource.erp2graph.ddl}")
    private String ddl;
    @Value("${spring.datasource.erp2graph.driverName}")
    private String driverName;
    @Value("${spring.jpa.properties.jadira.usertype.autoRegisterUserTypes}")
    private String autoRegisterUserTypes;

    public MysqlDatabaseConfiguration() {
        super();
    }

//    @Primary
    @Bean(name = "erp2graphEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(userDataSource());
        em.setPackagesToScan("at.ac.wuwien.causalminer.erp2graphdb");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", ddl);
        properties.put("hibernate.dialect", driverName);
        properties.put("jadira.usertype.autoRegisterUserTypes", autoRegisterUserTypes);
        em.setJpaPropertyMap(properties);

        return em;
    }

//    @Primary
    @Bean(name = "erp2graphUserDataSource")
    @ConfigurationProperties(prefix="spring.datasource.erp2graph")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

//    @Primary
    @Bean(name = "erp2graphTransactionManager")
    public PlatformTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager().getObject());
        return transactionManager;
    }

}
