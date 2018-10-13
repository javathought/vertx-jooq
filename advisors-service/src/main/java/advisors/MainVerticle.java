package advisors;

import advisors.handlers.GetAdvisorsHandler;
import advisors.handlers.PostAccountSqlHandler;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.asyncsql.MySQLClient;

//import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import advisors.handlers.GetHeartbeatHandler;
import advisors.handlers.PostAccountHandler;
import advisors.security.handlers.TokenSecurityHandler;
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
    private static final String CORS = "cors";
    private HttpServer server;
    private AsyncSQLClient mySQLClient;
    private DSLContext jooqContext;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        config().put("connection_string", System.getenv("DATABASE_URL"));
        config().put("db_name", System.getenv("DB_NAME"));



        JsonObject mySQLClientConfig = new JsonObject()
                .put("host", "localhost")
                .put("database", "bank")
                .put("username", "bank_adm")
                .put("password", "password");
        mySQLClient = MySQLClient.createShared(this.vertx, mySQLClientConfig);


        String userName = "bank_adm";
        String password = "password";
        String url = "jdbc:mysql://localhost:3306/bank";

        // Connection is the only JDBC resource that we need
        // PreparedStatement and ResultSet are handled by jOOQ, internally
        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
            jooqContext = DSL.using(conn, SQLDialect.MYSQL);
        }

        // For the sake of this tutorial, let's keep exception handling simple
        catch (SQLException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void start(Future<Void> future) {

        int port = config().getInteger(HTTP_PORT, DEFAULT_PORT);
        String hostListen = config().getString(HTTP_SERVER, DEFAULT_HOST);
        boolean cors = config().getBoolean(CORS, false);

        OpenAPI3RouterFactory.create(this.vertx, "/smarttpe-swagger.yaml", openAPI3RouterFactoryAsyncResult -> {
            if (openAPI3RouterFactoryAsyncResult.succeeded()) {
                try {
                    OpenAPI3RouterFactory routerFactory = openAPI3RouterFactoryAsyncResult.result();

                    MongoClient mongoClient = MongoClient.createShared(vertx, config());

                    // Enable automatic response when ValidationException is thrown
                    routerFactory.enableValidationFailureHandler(true);

                    // Add routes handlers
                    routerFactory.addHandlerByOperationId("getHeartbeat", new GetHeartbeatHandler());
                    routerFactory.addHandlerByOperationId("getAdvisors", new GetAdvisorsHandler(mySQLClient));
                    routerFactory.addHandlerByOperationId("postAccount", new PostAccountSqlHandler(jooqContext));

                    // Add security handlers
                    routerFactory.addSecurityHandler("Token", new TokenSecurityHandler());

                    // Generate the router
                    Router router = routerFactory.getRouter();

//                    router.route().order(1).handler(CookieHandler.create());

                    router.get("/swagger.yaml").handler(c -> c.response().rxSendFile(getClass().getResource("/smarttpe-swagger.yaml").getFile()).subscribe());

                    server = vertx.createHttpServer(new HttpServerOptions().setPort(port).setHost(hostListen));
                    server.requestHandler(router::accept).rxListen().subscribe(s -> LOG.info("Server started and listening on adress '{}' and port {}", hostListen, port));
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
        this.server.close();
    }


}
