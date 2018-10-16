package advisors.handlers;

import advisors.dao.tables.daos.AccountsDao;
import advisors.dao.tables.pojos.Accounts;
import io.reactivex.Single;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

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

//                c.dsl()
            try {
        dslContext.transaction( c-> {
                DSL.using(c)
                        .insertInto(ACCOUNTS).values(accountPojo.getAccountNumber(), accountPojo.getBalance())
                        .execute();
                JsonObject rec = DSL.using(c).selectFrom(ACCOUNTS)
                        .where(ACCOUNTS.BALANCE.eq(accountPojo.getBalance()))
                        .fetchOne().toJson();
                Accounts acc = new Accounts(rec).setBalance(BigDecimal.TEN);
                routingContext.response().setStatusCode(201).end(acc.toJson().encodePrettily());


//            res.result().toJson()
                }
        );
            } catch (Exception e) {
                LOG.error("Error saving account", e);
                routingContext.response().setStatusCode(500).end(new JsonObject().put("error", "something went wrong").encodePrettily());
            }

    }

}
