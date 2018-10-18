package advisors.services;

import advisors.services.impl.AccountsServiceImpl;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static advisors.MainVerticle.CRED_FIELD;
import static advisors.MainVerticle.DB_URL;
import static advisors.MainVerticle.USERNAME;

public class ServicesVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ServicesVerticle.class);

    public static final String ACCOUNTS_SERVICE_ADDRESS = "accounts.service";
    private MessageConsumer<JsonObject> accountConsumer;
    private AccountsService accountsService;
    private DSLContext jooqContext;
    private Connection conn;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        String userName = config().getString(USERNAME);
        String password = config().getString(CRED_FIELD);
        String url = config().getString(DB_URL);


        // Connection is the only JDBC resource that we need
        // PreparedStatement and ResultSet are handled by jOOQ, internally
        try {
            conn = DriverManager.getConnection(url, userName, password);
            conn.setAutoCommit(false);
            jooqContext = DSL.using(conn, SQLDialect.MYSQL);
        } catch (SQLException e) {
            LOG.error("Unable to init connection", e);
        }
        accountsService = new AccountsServiceImpl(jooqContext);
    }

    @Override
    public void start(Future<Void> startFuture) {
        
        accountConsumer = new ServiceBinder(vertx.getDelegate())
                .setAddress(ACCOUNTS_SERVICE_ADDRESS)
                .register(AccountsService.class, accountsService);
        accountConsumer.completionHandler(startFuture);


        LOG.info("Services started");
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        accountConsumer.unregister(stopFuture);
        try {
            this.conn.close();
        } catch (SQLException e) {
            LOG.error("exception on connection close", e);
        }
    }
}
