package com.zone01.buy01.discovery_server.config;

import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import com.netflix.discovery.Jersey3DiscoveryClientOptionalArgs;
import com.netflix.discovery.shared.transport.jersey.TransportClientFactories;
import com.netflix.discovery.shared.transport.jersey3.Jersey3TransportClientFactories;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Spring Cloud's auto-configuration only creates a TransportClientFactories bean
// when Jersey is ABSENT from the classpath (it falls back to RestTemplate).
// The Eureka *server* dashboard needs Jersey for its own UI, so Jersey is always
// present here, and the auto-config's RestTemplate-based bean never gets created.
// We wire the Jersey3-based factory manually instead of relying on that condition.
@Configuration
public class EurekaClientConfig {

    @Bean
    @ConditionalOnMissingBean(AbstractDiscoveryClientOptionalArgs.class)
    public Jersey3DiscoveryClientOptionalArgs jersey3DiscoveryClientOptionalArgs() {
        return new Jersey3DiscoveryClientOptionalArgs();
    }

    @Bean
    @ConditionalOnMissingBean(TransportClientFactories.class)
    public TransportClientFactories<?> transportClientFactories() {
        return Jersey3TransportClientFactories.getInstance();
    }
}