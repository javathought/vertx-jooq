package advisors.handlers;

import advisors.dao.tables.daos.AccountsDao;
import advisors.dao.tables.interfaces.IAccounts;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;

import java.util.stream.Collectors;

public class GetAccountsHandler implements Handler<RoutingContext> {

    private final AsyncSQLClient mySqlClient;
    private final AccountsDao withDao;

    public GetAccountsHandler(AsyncSQLClient sqlClient) {
        this.mySqlClient = sqlClient;
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.MYSQL);

        withDao = new AccountsDao(configuration, mySqlClient);

    }

    public void handle(RoutingContext routingContext) {

        withDao.findAll()
                .doOnSuccess(accounts ->
                        routingContext.response().end(
                                new JsonArray(
                                        accounts.stream().map(IAccounts::toJson).collect(Collectors.toList())
                                ).encodePrettily()
                        )
                )
                .doOnError(throwable ->
                        routingContext.response().setStatusCode(500)
                                .end(new JsonObject().put("error3", throwable.getLocalizedMessage()).encodePrettily()))

                .subscribe();

    }

}
