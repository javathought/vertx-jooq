package advisors.metrics;

import java.math.BigDecimal;

public class DoubleMetric {
    private BigDecimal delegate;

    public DoubleMetric(String s) {
        this.delegate = new BigDecimal(s);
    }

    public void setValue(BigDecimal delegate) {
        this.delegate = delegate;
    }

    public double doubleValue() {
        return this.delegate.doubleValue();
    }

}
