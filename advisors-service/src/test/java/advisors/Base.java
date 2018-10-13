package advisors;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class Base {

    Vertx vertx;
    String deploymentId;
    ApiClient apiClient;

    public void before(TestContext context) {
        vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(Long.MAX_VALUE));
        Async async = context.async();
        vertx.deployVerticle(MainVerticle.class.getName(), res -> {
            if (res.succeeded()) {
                deploymentId = res.result();
                apiClient = new ApiClient(vertx, "localhost", 9999);
                async.complete();
            } else {
                context.fail("Verticle deployment failed!");
                async.complete();
            }
        });
    }

    public void after(TestContext context) {
        apiClient.close();
        vertx.close(context.asyncAssertSuccess());
    }
}
