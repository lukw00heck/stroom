package stroom.elastic.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import javax.inject.Inject;

@Configuration
@ComponentScan(basePackages = {"stroom.elastic.server"}, excludeFilters = {
        // Exclude other configurations that might be found accidentally during
        // a component scan as configurations should be specified explicitly.
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class),})
public class ElasticIndexConfiguration {
    public ElasticIndexConfiguration() {
    }
}
