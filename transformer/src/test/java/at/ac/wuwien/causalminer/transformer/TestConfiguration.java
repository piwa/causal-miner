package at.ac.wuwien.causalminer.transformer;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
@ComponentScan(basePackages = {"at.ac.wuwien.causalminer.transformer"})
public class TestConfiguration {
}
