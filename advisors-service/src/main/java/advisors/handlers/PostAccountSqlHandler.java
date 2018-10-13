package advisors.handlers;

import advisors.jooq.tables.pojos.Accounts;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.ext.sql.SQLClient;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static advisors.jooq.Tables.ACCOUNTS;

public class PostAccountSqlHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(PostAccountSqlHandler.class);
    private final DSLContext dslContext;

//    private final SQLClient sqlClient;

//    public PostAccountSqlHandler(SQLClient sqlClient) {
//        this.sqlClient = sqlClient;
//    }

    public PostAccountSqlHandler(DSLContext jooqContext) {
        dslContext = jooqContext;

    }

    public void handle(RoutingContext routingContext) {

        JsonObject account = routingContext.getBodyAsJson();

        Accounts accountPojo = new Accounts(account);
        LOG.info("account = {}", accountPojo);

        dslContext.insertInto(ACCOUNTS).values(accountPojo.getAccountNumber(), accountPojo.getBalance());


//        accounts.



//        sqlClient.rxGetConnection().flatMap(sqlConnection -> {
//            return sqlConnection.rxExecute("INSERT INTO accounts (account_number, balance) VALUES ('FR76001', 5.3)")
//                    .andThen(sqlConnection.rxExecute("INSERT INTO accounts (account_number, balance) VALUES ('FR76002', 47.48)"))
//                    .andThen(sqlConnection.rxExecute("INSERT INTO accounts (account_number, balance) VALUES ('FR76002', 5.1)"))
//                    .andThen(sqlConnection.rxExecute("INSERT INTO accounts (account_number, balance) VALUES ('FR76003', 1396.32)"))
//                    .andThen(sqlConnection.rxExecute("INSERT INTO accounts (account_number, balance) VALUES ('FR76004', 2496.32)"))
////                    .andThen(sqlConnection.rxExecute("INSERT INTO tracks (album, name) VALUES ('The Israelites', 'Too Much Too Soon')"))
//                    .andThen(sqlConnection.rxQuery("SELECT account_number, balance FROM accounts WHERE account_number = 'FR76001'")
//                            .map(ResultSet::getRows))
////                    .compose(SQLClientHelper.txSingleTransformer(sqlConnection))
//                    .doFinally(sqlConnection::close);
////        }).map(rows -> {
////            // Transform DB rows into a client-friendly JSON object
////            rows
//        }).subscribe(json -> {
//            // Send JSON to the client
//            routingContext.response().setStatusCode(201).end(account.encodePrettily());
//        }, t -> {
//            // Send error to the client
//            routingContext.response().setStatusCode(500).setStatusMessage("Something went wrong").end();
//        });

        }

}
