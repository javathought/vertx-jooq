package advisors.handlers;

import advisors.dao.tables.daos.AccountsDao;
import advisors.dao.tables.pojos.Accounts;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;

import java.util.Arrays;
import java.util.List;

public class PostManyAccounts implements Handler<RoutingContext> {

    private final AccountsDao withDao;

    public PostManyAccounts(AsyncSQLClient asyncSQLClient) {
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.MYSQL); //or SQLDialect.POSTGRES
        withDao = new AccountsDao(configuration, asyncSQLClient);

    }

    public void handle(RoutingContext routingContext) {

        String json = routingContext.getBodyAsString();
        List<Accounts> accounts = Arrays.asList(Json.decodeValue(json, Accounts[].class));

        withDao
                .insert(accounts)
                .doOnSuccess(integer ->
                        routingContext.response()
                                .setStatusCode(201)
                                .end(new JsonObject().put("items_created",integer).encodePrettily())
                ).doOnError(throwable ->
                routingContext.response().setStatusCode(500).end(new JsonObject().put("error3", throwable.getLocalizedMessage()).encodePrettily()))
                .subscribe();

    }

}
