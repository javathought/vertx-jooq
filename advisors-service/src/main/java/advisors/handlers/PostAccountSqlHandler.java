package advisors.handlers;

import advisors.dao.tables.pojos.Accounts;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static advisors.dao.Tables.ACCOUNTS;

public class PostAccountSqlHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(PostAccountSqlHandler.class);
    private final DSLContext dslContext;

    public PostAccountSqlHandler(DSLContext jooqContext) {
        dslContext = jooqContext;

    }

    public void handle(RoutingContext routingContext) {

        JsonObject account = routingContext.getBodyAsJson();

        Accounts accountPojo = new Accounts(account);
        LOG.info("account = {}", accountPojo);

        dslContext.transaction( c->
//                c.dsl()
                DSL.using(c)
                        .insertInto(ACCOUNTS).values(accountPojo.getAccountNumber(), accountPojo.getBalance()).execute()
        );
        routingContext.response().setStatusCode(201).end(account.encodePrettily());

        }

}
