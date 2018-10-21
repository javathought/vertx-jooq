package advisors;

import advisors.handlers.*;
import advisors.security.handlers.TokenSecurityHandler;
import advisors.services.ServicesVerticle;
import advisors.services.reactivex.AccountsService;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.micrometer.backends.BackendRegistries;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.asyncsql.MySQLClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.reactivex.micrometer.MetricsService;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
    private static final int DEFAULT_PORT = 9998;
    private static final String DEFAULT_HOST = "0.0.0.0";
    // Configuration keys
    private static final String HTTP_PORT = "http.port";
    private static final String HTTP_SERVER = "http.server";
    public static final String CRED_FIELD = "password";
    public static final String USERNAME = "username";
    private static final String DATABASE = "db_name";
    public static final String DB_URL = "db_url";
    private HttpServer server;
    private AsyncSQLClient mySQLClient;
    private DSLContext jooqContext;
    private Connection conn;
    private JsonObject withSQLClientConfig;
    private AccountsService withAccountsService;

    @Override
    public void init(io.vertx.core.Vertx vertx, Context context) {

        super.init(vertx, context);
        config().put("connection_string", System.getenv("DATABASE_URL"));
        config().put(DATABASE, System.getenv("DB_NAME"));


        withSQLClientConfig = new JsonObject()
                .put("host", "localhost")
                .put("database", config().getString(DATABASE))
                .put(USERNAME, config().getString(USERNAME))
                .put(CRED_FIELD, config().getString(CRED_FIELD))
        .put("autocommit", "false")
        ;
        mySQLClient = MySQLClient.createNonShared(this.vertx, withSQLClientConfig);

        withAccountsService = new AccountsService(new ServiceProxyBuilder(Vertx.currentContext().owner().getDelegate())
                .setAddress(ServicesVerticle.ACCOUNTS_SERVICE_ADDRESS)
                .build(advisors.services.AccountsService.class));


        String userName = config().getString(USERNAME);
        String password = config().getString(CRED_FIELD);
        String url = config().getString(DB_URL);


        // Connection is the only JDBC resource that we need
        // PreparedStatement and ResultSet are handled by jOOQ, internally
        try {
            conn = DriverManager.getConnection(url, userName, password);
            conn.setAutoCommit(false);
            jooqContext = DSL.using(conn, SQLDialect.MYSQL);
        }

        // For the sake of this tutorial, let's keep exception handling simple
        catch (SQLException e) {
            LOG.error("Unable to init connection", e);
        }

    }

    @Override
    public void start(Future<Void> future) {

        int port = config().getInteger(HTTP_PORT, DEFAULT_PORT);
        String hostListen = config().getString(HTTP_SERVER, DEFAULT_HOST);

        /*
         * Deploy services
         */

        vertx.deployVerticle(
                "advisors.services.ServicesVerticle",
                new DeploymentOptions().setWorker(true).setConfig(config()),
                r -> {
                    if (r.succeeded()) {
                        LOG.info("child services started");
                    } else {
                        throw new VertxException("Unable to start services Verticle");
                    }
                }
        );



        OpenAPI3RouterFactory.create(this.vertx, "/smarttpe-swagger.yaml", openAPI3RouterFactoryAsyncResult -> {
            if (openAPI3RouterFactoryAsyncResult.succeeded()) {
                try {
                    OpenAPI3RouterFactory routerFactory = openAPI3RouterFactoryAsyncResult.result();

                    // Enable automatic response when ValidationException is thrown
                    routerFactory.enableValidationFailureHandler(true);

                    // Add routes handlers
                    routerFactory.addHandlerByOperationId("getHeartbeat", new GetHeartbeatHandler());
                    routerFactory.addHandlerByOperationId("getAccount", new GetAccountsHandler(mySQLClient));
                    routerFactory.addHandlerByOperationId("postAccount", new PostAccountSqlHandler(jooqContext));
                    routerFactory.addHandlerByOperationId("postAccounts", new PostAccountJooqNoAutoCommitHandler(withAccountsService));
                    routerFactory.addHandlerByOperationId("postManyAccounts", new PostManyAccounts(MySQLClient.createShared(this.vertx, withSQLClientConfig)));

                    // Add security handlers
                    routerFactory.addSecurityHandler("Token", new TokenSecurityHandler());

                    // Generate the router
                    Router router = routerFactory.getRouter();

                    router.get("/swagger.yaml").handler(c -> c.response().rxSendFile(getClass().getResource("/smarttpe-swagger.yaml").getFile()).subscribe());

                    router.route("/metrics").handler(routingContext -> {
                        PrometheusMeterRegistry prometheusRegistry = (PrometheusMeterRegistry) BackendRegistries.getDefaultNow();

                        if (prometheusRegistry != null) {
                            String response = prometheusRegistry.scrape();
                            routingContext.response().end(response);
                        } else {
                            routingContext.fail(500);
                        }
                    });

                    server = vertx.createHttpServer(new HttpServerOptions().setPort(port).setHost(hostListen));
                    server.requestHandler(router::accept).rxListen().subscribe(s -> LOG.info("Server started and listening on adress '{}' and port {}", hostListen, port)).dispose();
                    MetricsService.create(vertx);
                    MetricsService.create(server);
                    MetricsService.create(vertx.eventBus());
                    future.complete();
                } catch (Exception e) {
                    future.fail(e);
                }
            } else {
                // Something went wrong during router factory initialization
                Throwable exception = openAPI3RouterFactoryAsyncResult.cause();
                future.fail(exception);
            }
        });
    }

    @Override
    public void stop() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            LOG.error("exception on connection close", e);
        }
        this.server.close();
    }


}
