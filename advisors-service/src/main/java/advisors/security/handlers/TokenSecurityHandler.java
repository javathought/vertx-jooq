package advisors.security.handlers;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

public class TokenSecurityHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext routingContext) {
        // Handle Token security schema
        routingContext.next();
    }

}