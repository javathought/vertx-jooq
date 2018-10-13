package advisors.handlers;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAdvisorsHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(GetAdvisorsHandler.class);

    private final AsyncSQLClient mySqlClient;

    public GetAdvisorsHandler(AsyncSQLClient mongoClient) {
        this.mySqlClient = mongoClient;
    }

    public void handle(RoutingContext routingContext) {

        JsonObject query = new JsonObject();
        routingContext.response().setStatusCode(503).setStatusMessage("Not implemented").end();

//        mySqlClient.rxFind("advisors", query)
//                .subscribe(r -> {
//                    LOG.trace("mongo response [{}]", r);
//                    routingContext.response().setStatusCode(200).end(new JsonArray(r).encodePrettily());
//                        },
//                        r -> routingContext.response().setStatusCode(500).setStatusMessage("Something went wrong").end());


    }

}
