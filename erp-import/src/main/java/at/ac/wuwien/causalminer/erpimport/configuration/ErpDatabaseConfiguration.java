package at.ac.wuwien.causalminer.erpimport.configuration;

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

@Configuration
@EnableTransactionManagement
//@Profile({ "production" })
@EnableJpaRepositories(basePackages = "at.ac.wuwien.causalminer.erpimport.repositories", entityManagerFactoryRef = "erpEntityManager", transactionManagerRef = "erpTransactionManager")
@PropertySources({
        @PropertySource("classpath:configuration/connection.properties"),
        @PropertySource("classpath:configuration/credentials.properties"),
})
public class ErpDatabaseConfiguration {

    @Value("${spring.datasource.erp.ddl}")
    private String ddl;
    @Value("${spring.datasource.erp.driverName}")
    private String driverName;
    @Value("${spring.jpa.properties.jadira.usertype.autoRegisterUserTypes}")
    private String autoRegisterUserTypes;

    public ErpDatabaseConfiguration() {
        super();
    }

    @Primary
    @Bean(name = "erpEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(userDataSource());
        em.setPackagesToScan("at.ac.wuwien.causalminer.erpimport");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", ddl);
        properties.put("hibernate.dialect", driverName);
        properties.put("jadira.usertype.autoRegisterUserTypes", autoRegisterUserTypes);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean(name = "erpUserDataSource")
    @ConfigurationProperties(prefix="spring.datasource.erp")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "erpTransactionManager")
    public PlatformTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager().getObject());
        return transactionManager;
    }

}
