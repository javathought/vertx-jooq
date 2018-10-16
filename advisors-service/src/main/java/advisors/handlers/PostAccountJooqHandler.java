package advisors.handlers;

import advisors.dao.tables.daos.AccountsDao;
import advisors.dao.tables.pojos.Accounts;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static advisors.dao.tables.Accounts.ACCOUNTS;

public class PostAccountJooqHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(PostAccountJooqHandler.class);
    private final AccountsDao withDao;
    private final AsyncSQLClient client;

    public PostAccountJooqHandler(AsyncSQLClient asyncSQLClient) {
//Setup your jOOQ configuration
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.MYSQL); //or SQLDialect.POSTGRES
//        configuration.set
        client = asyncSQLClient;
        withDao = new AccountsDao(configuration, asyncSQLClient);

    }

    public void handle(RoutingContext routingContext) {

        JsonObject account = routingContext.getBodyAsJson();

        Accounts accountPojo = new Accounts(account);
        LOG.info("account = {}", accountPojo);

        withDao
                .insert(accountPojo)
                .doOnSuccess(integer ->
                        withDao.findOneByCondition(ACCOUNTS.BALANCE.eq(accountPojo.getBalance()))
                                .doOnError(throwable ->
                                        routingContext.response()
                                                .setStatusCode(500)
                                                .end(new JsonObject().put("error2", throwable.getLocalizedMessage()).encodePrettily()))
                                .doOnEvent(
                                        (opt, x) -> {
                                            LOG.info("db response ...");
                                            if (x == null) {
                                                LOG.info("db response OK ....");
                                                if (opt.isPresent()) {
                                                    routingContext.response()
                                                            .setStatusCode(201)
                                                            .end(opt.get().toJson().encodePrettily());
                                                } else {
                                                    routingContext.response()
                                                            .setStatusCode(404).end();
                                                }
//                                                JsonArray res = new JsonArray(opt.stream().map(VertxPojo::toJson).collect(Collectors.toList()));
//                                                routingContext.response().setStatusCode(201).end(res.encodePrettily());
                                            }

                                        }
                                )
                                .subscribe()
                ).doOnError(throwable ->
                routingContext.response().setStatusCode(500).end(new JsonObject().put("error3", throwable.getLocalizedMessage()).encodePrettily()))
                .subscribe();

    }

}
