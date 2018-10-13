package advisors.handlers;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;

public class GetHeartbeatHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(GetHeartbeatHandler.class);
    
    @Override
    public void handle(RoutingContext routingContext) {
        LOG.trace("asking heartbeat");
        // Handle getHeartbeat
        routingContext.response().putHeader(CONTENT_TYPE.toString(), TEXT_PLAIN.toString());
        routingContext.response().setStatusCode(200).end(beat());
    }

    private String beat() {
        Instant t = Instant.now();
        return String.format("OK - I'm alive (%d,%d)", t.getEpochSecond(), t.getNano());
    }
    
}