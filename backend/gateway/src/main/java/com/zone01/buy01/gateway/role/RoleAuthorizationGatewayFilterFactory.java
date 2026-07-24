package com.zone01.buy01.gateway.role;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Route-level authorization on top of the global JWT filter.
 * The JWT filter proves WHO the caller is (X-User-Role header);
 * this filter decides WHAT that role is allowed to hit.
 *
 * Usage in application.yml route filters:
 *   - name: RoleAuthorization
 *     args:
 *       roles: ADMIN
 */
@Component
public class RoleAuthorizationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RoleAuthorizationGatewayFilterFactory.Config> {

    public RoleAuthorizationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        List<String> allowedRoles = List.of(config.getRoles().split(","));

        return (exchange, chain) -> {
            String role = exchange.getRequest().getHeaders().getFirst("X-User-Role");

            if (role == null || !allowedRoles.contains(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
        private String roles; // comma-separated, e.g. "ADMIN,MODERATOR"

        public String getRoles() { return roles; }
        public void setRoles(String roles) { this.roles = roles; }
    }
}
