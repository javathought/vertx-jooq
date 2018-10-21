package advisors.handlers;

import advisors.services.reactivex.AccountsService;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

public class PostAccountOnEBHandler implements Handler<RoutingContext> {

    private final AccountsService accountsService;

    public PostAccountOnEBHandler(AccountsService accountsService) {
        this.accountsService = accountsService;        
    }

    public void handle(RoutingContext routingContext) {

        accountsService.rxPostAccount(routingContext.getBodyAsJson())
                .subscribe(r -> routingContext.response().setStatusCode(200).end(r.encodePrettily()),
                        r -> routingContext.response().setStatusCode(500).setStatusMessage("Something went wrong").end()                       
                ).dispose();


    }

}
