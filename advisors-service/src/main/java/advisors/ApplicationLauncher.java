package advisors;

import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;

public class ApplicationLauncher extends Launcher {

    public static void main(String[] args) {
        ApplicationLauncher application = new ApplicationLauncher();
        application.dispatch(args);
    }


    @Override
    public void beforeStartingVertx(VertxOptions options) {

        options.setMetricsOptions(
                new MicrometerMetricsOptions()
                        .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true))
                        .setEnabled(true));

        super.beforeStartingVertx(options);
    }
}
