package advisors.handlers;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostAccountHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(PostAccountHandler.class);

    private final AsyncSQLClient mongoClient;

    public PostAccountHandler(AsyncSQLClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void handle(RoutingContext routingContext) {

        JsonObject account = routingContext.getBodyAsJson();

        routingContext.response().setStatusCode(503).setStatusMessage("Not implemented").end();

//        mongoClient.rxSave("accounts", account)
//                .subscribe(r -> {
//                    LOG.trace("mongo response [{}]", r);
//                    account.put("_id", r);
//                    routingContext.response().setStatusCode(201).end(account.encodePrettily());
//                        },
//                        r -> routingContext.response().setStatusCode(500).setStatusMessage("Something went wrong").end());
    }

}
