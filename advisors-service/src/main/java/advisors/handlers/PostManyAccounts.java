package advisors.handlers;

import advisors.dao.tables.daos.AccountsDao;
import advisors.dao.tables.pojos.Accounts;
import advisors.metrics.DoubleMetric;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.micrometer.backends.BackendRegistries;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class PostManyAccounts implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(PostManyAccounts.class);

    private final AccountsDao withDao;
    private final Timer timer;
    private DoubleMetric counter = new DoubleMetric("1.3");

    public PostManyAccounts(AsyncSQLClient asyncSQLClient) {
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.MYSQL); //or SQLDialect.POSTGRES
        withDao = new AccountsDao(configuration, asyncSQLClient);

        MeterRegistry registry = BackendRegistries.getDefaultNow();

        registry.gauge("advisor-service.balance.post.many", counter, DoubleMetric::doubleValue);

        timer = Timer
                .builder("advisor-service.timer.post.many")
                .description("execution time of posting many accounts")
                .register(registry);

    }

    public void handle(RoutingContext routingContext) {

        timer.record(() -> {
            JsonArray arr = routingContext.getBodyAsJsonArray();
            List<Accounts> accounts = arr.stream()
                    .map(j -> new Accounts((JsonObject)j)).collect(Collectors.toList());

        counter.setValue(accounts.stream().map(Accounts::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add));
        LOG.info("total balance = {}", counter.doubleValue());

        withDao
                .insert(accounts)
                .doOnSuccess(integer ->
                        routingContext.response()
                                .setStatusCode(201)
                                .end(new JsonObject().put("items_created",integer).encodePrettily())
                ).doOnError(throwable ->
                routingContext.response().setStatusCode(500)
                        .end(new JsonObject().put("error3", throwable.getLocalizedMessage()).encodePrettily()))
                .subscribe();
        });

    }

}
