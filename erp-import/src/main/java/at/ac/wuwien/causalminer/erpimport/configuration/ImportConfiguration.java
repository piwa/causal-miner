package at.ac.wuwien.causalminer.erpimport.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan(value= "at.ac.wuwien.causalminer")
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:application_import.properties")
})
public class ImportConfiguration {
}
