package advisors.handlers;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

public class PostAccountHandler implements Handler<RoutingContext> {

    public void handle(RoutingContext routingContext) {

        routingContext.response().setStatusCode(503).setStatusMessage("Not implemented").end();

    }

}
