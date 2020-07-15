package at.ac.wuwien.causalminer.transformer.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan(value= "at.ac.wuwien.causalminer")
@PropertySources({
        @PropertySource("classpath:application_transformer.properties"),
})
public class TransformerConfiguration {
}
