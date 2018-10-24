package advisors;

import io.vertx.core.AsyncResult;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.client.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * getHeartbeat Test
 */
public class GetHeartbeat extends Base {

    @Override
    @Before
    public void before(TestContext context) {
        super.before(context);
    }

    @Override
    @After
    public void after(TestContext context) {
        super.after(context);
    }

    @Test
    public void test200(TestContext test) {
        Async async = test.async(1);

        apiClient.getHeartbeat((AsyncResult<HttpResponse> ar) -> {
            if (ar.succeeded()) {
                test.verify( v -> {
                    assertThat(ar.result().statusCode()).isEqualTo(200);
                    assertThat(ar.result().bodyAsString()).startsWith("OK - I'm alive (");
                });
            } else {
                test.fail("Request failed");
            }
            async.countDown();
        });
    }


}