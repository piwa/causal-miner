package at.ac.wuwien.causalminer.frontend.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan(value= "at.ac.wuwien.causalminer")
@ConfigurationPropertiesScan
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:configuration/style/node.properties")
})
public class FrontendMainConfiguration {
}
