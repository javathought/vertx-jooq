package advisors.handlers;

import advisors.domain.ports.primary.AccountManager;
import advisors.infra.mapping.AccountJsonMapper;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.stream.Collectors;

public class GetAccountsHandler implements Handler<RoutingContext> {

    private final AccountManager accountMgr;

    public GetAccountsHandler(AccountManager accountMgr) {
        this.accountMgr = accountMgr;
    }

    public void handle(RoutingContext routingContext) {

        accountMgr.getAll()
                .doOnSuccess(accounts ->
                            routingContext.response().end(
                                    new JsonArray(
                                            accounts.stream().map(AccountJsonMapper::toJson).collect(Collectors.toList())
                                    ).encodePrettily()
                            )
                        
                )
                .doOnError(throwable ->
                        routingContext.response().setStatusCode(500)
                                .end(new JsonObject().put("error3", throwable.getLocalizedMessage()).encodePrettily()))

                .subscribe();

    }

}
