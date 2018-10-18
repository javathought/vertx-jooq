package advisors.services.impl;

import advisors.dao.tables.pojos.Accounts;
import advisors.services.AccountsService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static advisors.dao.Tables.ACCOUNTS;

public class AccountsServiceImpl implements AccountsService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsServiceImpl.class);
    private DSLContext dslContext;

    public AccountsServiceImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public AccountsService postAccount(JsonObject account, Handler<AsyncResult<JsonObject>> resultHandler) {

        Accounts accountPojo = new Accounts(account);
        LOG.info("account = {}", accountPojo);

        try {
            dslContext.transaction( c-> {
                        DSL.using(c)
                                .insertInto(ACCOUNTS).values(accountPojo.getAccountNumber(), accountPojo.getBalance())
                                .execute();
                        JsonObject rec = DSL.using(c).selectFrom(ACCOUNTS)
                                .where(ACCOUNTS.BALANCE.eq(accountPojo.getBalance()))
                                .fetchOne().toJson();

                        resultHandler.handle(Future.succeededFuture(rec));

                    }
            );
        } catch (Exception e) {
            LOG.error("Error saving account", e);
            resultHandler.handle(Future.failedFuture(e));
        }
       
        
        return this;
    }

}
